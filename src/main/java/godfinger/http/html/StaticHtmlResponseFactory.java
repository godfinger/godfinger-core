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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import godfinger.http.HttpResponseFactory;
import godfinger.http.rest.exception.NotFoundException;
import godfinger.util.FileUtil;
import godfinger.util.InputStreamUtil;
import io.netty.handler.codec.http.HttpResponse;

import static godfinger.util.NettyHttpUtil.fullHttpResponse;
import static godfinger.util.StackTraceUtil.stringValueOf;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class StaticHtmlResponseFactory implements HttpResponseFactory {

  private final Map<String, String> staticHtmlCache = new ConcurrentHashMap<>();
  private final String directoryResourceString;
  private final Class<?> classForResourceLoading;

  public StaticHtmlResponseFactory(String directoryResourceString, Class<?> classForResourceLoading) throws IOException {
    if (!directoryResourceString.endsWith("/")) {
      directoryResourceString += "/";
    }
    this.directoryResourceString = directoryResourceString;
    this.classForResourceLoading = classForResourceLoading;
  }

  @Override
  public HttpResponse createHttpResponse(Throwable t) {
    if (t instanceof NotFoundException) {
      return fullHttpResponse(NOT_FOUND, "text/plain", stringValueOf(t));
    } else {
      return fullHttpResponse(INTERNAL_SERVER_ERROR, "text/plain", stringValueOf(t));
    }
  }

  @Override
  public HttpResponse createHttpResponse(Object result) {
    if (!(result instanceof Html)) {
      throw new IllegalArgumentException("result is not an instance of Html");
    }
    Html html = (Html) result;

    String fileName = html.getFileName();
    String resourceString = directoryResourceString + fileName;
    String htmlString = staticHtmlCache.get(fileName);
    if (htmlString == null) {
      try {
        InputStream is = InputStreamUtil.openInputStream(resourceString, classForResourceLoading.getClassLoader());
        if (is == null) {
          throw new FileNotFoundException(fileName);
        }
        htmlString = FileUtil.readFile(is);
      } catch (FileNotFoundException e) {
        return createHttpResponse(new NotFoundException((resourceString)));
      } catch (Exception e) {
        return createHttpResponse(e);
      }
      staticHtmlCache.put(fileName, htmlString);
    }

    return fullHttpResponse(OK, "text/html", htmlString);
  }

}
