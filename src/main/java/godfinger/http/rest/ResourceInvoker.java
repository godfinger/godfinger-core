/*
 * Copyright 2015 Godfinger Framework
 *
 * Godfinger Framework licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package godfinger.http.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import godfinger.http.HttpRequestProcessor;
import godfinger.http.HttpResponseFactory;
import godfinger.http.rest.exception.InvocationRejectedException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import static godfinger.util.NettyHttpUtil.fullHttpResponse;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Component
public class ResourceInvoker implements HttpRequestProcessor {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ResourceRegistry resourceRegistry;

  @Autowired
  public ResourceInvoker(ResourceRegistry resourceRegistry) {
    this.resourceRegistry = resourceRegistry;
  }

  @Override
  public HttpResponse process(FullHttpRequest request) {
    Resource resource = resourceRegistry.getResource(request.getMethod(), request.getUri());
    if (resource == null) {
      return fullHttpResponse(OK, "text/plain", NOT_FOUND + "\n\n" + request.getMethod() + " " + request.getUri());
    }

    HttpResponseFactory httpResponseFactory = resource.getHttpResponseFactory();
    try {
      InvocationContext context = new InvocationContext(resource, request);

      List<Interceptor> interceptors = resource.getInterceptors();
      for (Interceptor interceptor : interceptors) {
        interceptor.beforeInvocation(context);
        if (context.isAborted()) {
          return httpResponseFactory.createHttpResponse(context.getResult());
        }
      }

      ResourceArgumentsParser argumentsParser = resource.getArgumentsParser();
      Object[] arguments = argumentsParser.parse(context);
      context.setArguments(arguments);

      Object object = resource.getObject();
      Method method = resource.getMethod();
      Object result = method.invoke(object, arguments);
      context.setResult(result);

      for (int i = interceptors.size() - 1; i >= 0; i--) {
        Interceptor interceptor = interceptors.get(i);
        interceptor.afterInvocation(context);
        if (context.isAborted()) {
          return httpResponseFactory.createHttpResponse(context.getResult());
        }
      }

      return httpResponseFactory.createHttpResponse(result);
    } catch (InvocationTargetException e) {
      logger.error("Caught an exception:", e.getTargetException());
      return httpResponseFactory.createHttpResponse(e.getTargetException());
    } catch (InvocationRejectedException e) {
      logger.error("Invocation rejected: " + e);
      return httpResponseFactory.createHttpResponse(e);
    } catch (Throwable e) {
      logger.error("Caught an exception:", e);
      return httpResponseFactory.createHttpResponse(e);
    }
  }

}
