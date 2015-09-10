package godfinger.http.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import godfinger.util.UriUtil;
import io.netty.handler.codec.http.HttpMethod;

@Component
public class ResourceRegistry {

  private final Map<HttpMethod, List<Resource>> resourceGroups = new HashMap<>();

  @Autowired
  public ResourceRegistry(ResourceLoader resourceLoader) throws Exception {
    List<Resource> resources = resourceLoader.loadResources();
    groupResourcesByHttpMethod(resources);
  }

  public Resource getResource(HttpMethod method, String uri) {
    List<Resource> resourceGroup = resourceGroups.get(method);
    if (resourceGroup == null) {
      return null;
    }

    for (Resource resource : resourceGroup) {
      if (resource.matches(method, UriUtil.decodePath(uri))) {
        return resource;
      }
    }
    return null;
  }

  private void groupResourcesByHttpMethod(List<Resource> resources) {
    for (Resource resource : resources) {
      List<Resource> resourceGroup = resourceGroups.get(resource.getHttpMethod());
      if (resourceGroup == null) {
        resourceGroup = new ArrayList<>();
        resourceGroups.put(resource.getHttpMethod(), resourceGroup);
      }
      resourceGroup.add(resource);
    }
  }

}
