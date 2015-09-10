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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import godfinger.http.HttpResponseFactory;
import godfinger.http.rest.exception.NotFoundException;
import io.netty.handler.codec.http.HttpResponse;

import static freemarker.template.Configuration.VERSION_2_3_23;
import static freemarker.template.TemplateExceptionHandler.HTML_DEBUG_HANDLER;
import static godfinger.util.NettyHttpUtil.fullHttpResponse;
import static godfinger.util.StackTraceUtil.stringValueOf;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

public class DynamicHtmlResponseFactory implements HttpResponseFactory {

  private final Configuration configuration;

  public DynamicHtmlResponseFactory(String resourceString, Class<?> classForTemplateLoading) throws IOException {
    String templateDirectory = resourceString.substring(resourceString.indexOf(':') + 1);
    if (!templateDirectory.startsWith("/")) {
      templateDirectory = "/" + templateDirectory;
    }

    configuration = new Configuration(VERSION_2_3_23);
    if (resourceString.startsWith("classpath:")) {
      configuration.setClassForTemplateLoading(classForTemplateLoading, templateDirectory);
    } else {
      configuration.setDirectoryForTemplateLoading(new File(templateDirectory));
    }

    configuration.setObjectWrapper(new DefaultObjectWrapper(VERSION_2_3_23));
    configuration.setDefaultEncoding("UTF-8");
    configuration.setTemplateExceptionHandler(HTML_DEBUG_HANDLER);
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
    if (!(result instanceof HtmlTemplate)) {
      throw new IllegalArgumentException("result is not an instance of HtmlTemplate.");
    }
    HtmlTemplate htmlTemplate = (HtmlTemplate) result;

    String fileName = htmlTemplate.getFileName();

    try {
      Template template = configuration.getTemplate(fileName);
      try (StringWriter stringWriter = new StringWriter()) {
        template.process(htmlTemplate.getParameters(), stringWriter);
        String html = stringWriter.toString();
        return fullHttpResponse(OK, "text/html", html);
      }
    } catch (Exception e) {
      return createHttpResponse(e);
    }
  }

}
