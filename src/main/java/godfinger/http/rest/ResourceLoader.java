package godfinger.http.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import godfinger.http.HttpResponseFactory;
import godfinger.http.rest.annotation.Context;
import godfinger.http.rest.annotation.Cookie;
import godfinger.http.rest.annotation.Export;
import godfinger.http.rest.annotation.Header;
import godfinger.http.rest.annotation.HttpDelete;
import godfinger.http.rest.annotation.HttpGet;
import godfinger.http.rest.annotation.HttpPost;
import godfinger.http.rest.annotation.HttpPut;
import godfinger.http.rest.annotation.Json;
import godfinger.http.rest.annotation.JsonArray;
import godfinger.http.rest.annotation.Optional;
import godfinger.http.rest.annotation.Path;
import godfinger.http.rest.annotation.Query;
import godfinger.http.rest.annotation.With;
import godfinger.http.rest.exception.DuplicateResourceException;
import godfinger.http.rest.parameter.ContextParameterParser;
import godfinger.http.rest.parameter.CookieParameterParser;
import godfinger.http.rest.parameter.HeaderParameterParser;
import godfinger.http.rest.parameter.JsonArrayParameterParser;
import godfinger.http.rest.parameter.JsonParameterParser;
import godfinger.http.rest.parameter.PathParameterParser;
import godfinger.http.rest.parameter.QueryParameterParser;
import godfinger.http.rest.parameter.ResourceArgumentParser;
import godfinger.util.UriUtil;
import godfinger.util.cast.LexicalCaster;
import godfinger.util.cast.LexicalCasters;
import io.netty.handler.codec.http.HttpMethod;

@Component
public class ResourceLoader {

  private static final HttpResponseFactory DEFAULT_HTTP_RESPONSE_FACTORY = new JsendResponseFactory();

  @SuppressWarnings("serial")
  private static final Set<Class<?>> SUPPORTED_PARAM_ANNOTATION_TYPES = new HashSet<Class<?>>() {
    {
      add(Path.class);
      add(Query.class);
      add(Header.class);
      add(Cookie.class);
      add(Json.class);
      add(JsonArray.class);
      add(Context.class);
    }
  };

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ApplicationContext applicationContext;

  @Autowired
  public ResourceLoader(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public List<Resource> loadResources() throws Exception {
    Map<String, Resource> duplicateChecker = new HashMap<>();

    List<Resource> resources = new ArrayList<>();
    for (Object exportedObject : getExportedObjects()) {
      Class<?> exportedClass = exportedObject.getClass();
      if (logger.isDebugEnabled()) {
        logger.debug("Found an exported resource: " + exportedClass.getCanonicalName());
      }

      Export export = exportedClass.getAnnotation(Export.class);
      String basePath = UriUtil.normalizePath(export.value());

      List<Interceptor> interceptors = new ArrayList<>();
      With with = exportedClass.getAnnotation(With.class);
      if (with != null) {
        Class<? extends Interceptor>[] interceptorClasses = with.value();
        for (Class<? extends Interceptor> interceptorClass : interceptorClasses) {
          Interceptor interceptor = applicationContext.getBean(interceptorClass);
          interceptors.add(interceptor);
        }
      }

      for (Method method : exportedClass.getMethods()) {
        Resource resource = getResource(exportedObject, method, basePath, interceptors);
        if (resource != null) {
          String resourceKey = resource.getHttpMethod().name() + " " + resource.getPathString();
          Resource duplicate = duplicateChecker.get(resourceKey);
          if (duplicate != null) {
            throw new DuplicateResourceException(duplicate, resource);
          }
          duplicateChecker.put(resourceKey, resource);
          resources.add(resource);
        }
      }
    }

    logLoadedResources(duplicateChecker);

    return resources;
  }

  private void logLoadedResources(Map<String, Resource> duplicateChecker) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Resource> entry : duplicateChecker.entrySet()) {
      sb.append(entry.getKey()).append(" => ").append(entry.getValue().getMethod()).append("\n");
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
    }
    logger.info("Loaded resources: ({})\n{}", duplicateChecker.size(), sb.toString());
  }

  private Resource getResource(Object object, Method method, String basePath, List<Interceptor> interceptors)
          throws Exception {
    HttpPost post = method.getAnnotation(HttpPost.class);
    if (post != null) {
      String pathString = basePath + UriUtil.normalizePath(post.value());
      return createResource(HttpMethod.POST, pathString, object, method, interceptors);
    }

    HttpGet get = method.getAnnotation(HttpGet.class);
    if (get != null) {
      String pathString = basePath + UriUtil.normalizePath(get.value());
      return createResource(HttpMethod.GET, pathString, object, method, interceptors);
    }

    HttpPut put = method.getAnnotation(HttpPut.class);
    if (put != null) {
      String pathString = basePath + UriUtil.normalizePath(put.value());
      return createResource(HttpMethod.PUT, pathString, object, method, interceptors);
    }

    HttpDelete delete = method.getAnnotation(HttpDelete.class);
    if (delete != null) {
      String pathString = basePath + UriUtil.normalizePath(delete.value());
      return createResource(HttpMethod.DELETE, pathString, object, method, interceptors);
    }

    return null;
  }

  private Resource createResource(HttpMethod httpMethod, String pathString, Object object, Method method,
          List<Interceptor> interceptors) throws Exception {
    ResourceArgumentsParser parameterParser = buildParameterParser(method, pathString);
    HttpResponseFactory httpResponseFactory = DEFAULT_HTTP_RESPONSE_FACTORY;
    return new Resource(httpMethod, pathString, object, method, parameterParser, httpResponseFactory, interceptors);
  }

  private ResourceArgumentsParser buildParameterParser(Method method, String pathString) {
    List<ResourceArgumentParser> httpParameterParsers = new ArrayList<>();

    Annotation[][] annotationArrays = method.getParameterAnnotations();
    Class<?>[] dataTypes = method.getParameterTypes();
    for (int i = 0; i < annotationArrays.length; i++) {
      Annotation paramAnnotation = null;
      boolean optional = false;
      String defaultValue = null;
      for (Annotation annotation : annotationArrays[i]) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (SUPPORTED_PARAM_ANNOTATION_TYPES.contains(annotationType)) {
          paramAnnotation = annotation;
        } else if (annotation instanceof Optional) {
          optional = true;
          Optional optionalAnnotation = (Optional) annotation;
          defaultValue = optionalAnnotation.value();
          if (Optional.NULL.equals(defaultValue)) {
            defaultValue = null;
          }
        }
      }

      Class<?> dataType = dataTypes[i];
      LexicalCaster<?> lexicalCaster = LexicalCasters.get(dataType);

      ResourceArgumentParser httpParameter;
      if (paramAnnotation instanceof Query) {
        Query param = (Query) paramAnnotation;
        httpParameter = new QueryParameterParser(param.value(), lexicalCaster, optional, defaultValue);
      } else if (paramAnnotation instanceof Path) {
        Path path = (Path) paramAnnotation;
        httpParameter = new PathParameterParser(path.value(), pathString, lexicalCaster, optional, defaultValue);
      } else if (paramAnnotation instanceof Header) {
        Header header = (Header) paramAnnotation;
        httpParameter = new HeaderParameterParser(header.value(), lexicalCaster, optional, defaultValue);
      } else if (paramAnnotation instanceof Cookie) {
        Cookie cookie = (Cookie) paramAnnotation;
        httpParameter = new CookieParameterParser(cookie.value(), lexicalCaster, optional, defaultValue);
      } else if (paramAnnotation instanceof Json) {
        Json json = (Json) paramAnnotation;
        httpParameter = new JsonParameterParser(json.value(), dataType, optional, defaultValue);
      } else if (paramAnnotation instanceof JsonArray) {
        JsonArray jsonArray = (JsonArray) paramAnnotation;
        httpParameter = new JsonArrayParameterParser(jsonArray.value(), jsonArray.name(), dataType, optional, defaultValue);
      } else if (paramAnnotation instanceof Context) {
        Context context = (Context) paramAnnotation;
        httpParameter = new ContextParameterParser(context.value(), optional);
      } else {
        continue;
      }

      httpParameterParsers.add(httpParameter);
    }

    return new ResourceArgumentsParser(httpParameterParsers);
  }

  private Collection<Object> getExportedObjects() {
    Map<String, Object> beansWithExport = applicationContext.getBeansWithAnnotation(Export.class);
    return beansWithExport.values();
  }

}
