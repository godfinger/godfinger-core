package godfinger.http.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import godfinger.http.HttpResponseFactory;
import godfinger.http.rest.exception.InvocationFailedException;
import godfinger.http.rest.exception.InvocationRejectedException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;

import static godfinger.util.StackTraceUtil.stringValueOf;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class JsendResponseFactory implements HttpResponseFactory {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public HttpResponse createHttpResponse(Throwable t) {
    Map<String, Object> jsendResponse = new HashMap<>(3);
    if (t instanceof InvocationFailedException) {
      InvocationFailedException invocationFailed = (InvocationFailedException) t;
      jsendResponse.put("status", "fail");
      jsendResponse.put("data", invocationFailed.getErrors());
    } else if (t instanceof InvocationRejectedException) {
      InvocationRejectedException invocationRejected = (InvocationRejectedException) t;
      jsendResponse.put("status", "error");
      jsendResponse.put("message", invocationRejected.getMessage());
      if (invocationRejected.getCode() != null) {
        jsendResponse.put("code", invocationRejected.getCode());
      }
      if (invocationRejected.getData() != null) {
        jsendResponse.put("data", invocationRejected.getData());
      }
    } else {
      jsendResponse.put("status", "error");
      jsendResponse.put("message", t.getMessage());
      jsendResponse.put("data", stringValueOf(t));
    }

    return createHttpResponse(jsendResponse);
  }

  @Override
  public HttpResponse createHttpResponse(Object result) {
    Map<String, Object> jsendResponse = new HashMap<>(2);
    jsendResponse.put("status", "success");
    if (result != null) {
      jsendResponse.put("data", result);
    }

    return createHttpResponse(jsendResponse);
  }

  private HttpResponse createHttpResponse(Map<String, Object> jsendResponse) {
    String jsonContent;
    try {
      jsonContent = objectMapper.writeValueAsString(jsendResponse);
    } catch (JsonProcessingException e) {
      return createHttpResponse(e);
    }

    ByteBuf content = Unpooled.wrappedBuffer(jsonContent.getBytes());
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
    response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=utf-8");
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
    return response;
  }

}
