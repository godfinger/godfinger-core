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
package godfinger.http.html;

import java.util.HashMap;
import java.util.Map;

import godfinger.http.HttpResponseFactory;
import godfinger.http.html.annotation.WithHtmls;

public class HtmlLoader {

  private final Map<String, Map<Class<? extends HttpResponseFactory>, HttpResponseFactory>> responseFactoryCaches =
          new HashMap<>();

  private HttpResponseFactory getHtmlResponseFactory(Object object, Class<?> returnType) throws Exception {
    WithHtmls usingHtml = object.getClass().getAnnotation(WithHtmls.class);
    if (usingHtml == null) {
      return null;
    }

    Map<Class<? extends HttpResponseFactory>, HttpResponseFactory> responseFactoryCache =
            responseFactoryCaches.get(usingHtml.in());
    if (responseFactoryCache == null) {
      responseFactoryCache = new HashMap<>();
      responseFactoryCaches.put(usingHtml.in(), responseFactoryCache);
    }

    if (returnType == Html.class) {
      HttpResponseFactory responseFactory = responseFactoryCache.get(StaticHtmlResponseFactory.class);
      if (responseFactory == null) {
        responseFactory = new StaticHtmlResponseFactory(usingHtml.in(), object.getClass());
        responseFactoryCache.put(StaticHtmlResponseFactory.class, responseFactory);
      }
      return responseFactory;
    }

    if (returnType == Html.class) {
      HttpResponseFactory responseFactory = responseFactoryCache.get(DynamicHtmlResponseFactory.class);
      if (responseFactory == null) {
        responseFactory = new StaticHtmlResponseFactory(usingHtml.in(), object.getClass());
        responseFactoryCache.put(DynamicHtmlResponseFactory.class, responseFactory);
      }
      return responseFactory;
    }

    return null;
  }

}
