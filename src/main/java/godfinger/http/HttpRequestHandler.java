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
package godfinger.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final HttpRequestProcessor requestProcessor;

  public HttpRequestHandler(HttpRequestProcessor requestProcessor) {
    this.requestProcessor = requestProcessor;
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext context) {
    context.flush();
  }

  @Override
  public void channelRead0(ChannelHandlerContext context, FullHttpRequest request) {
    logger.trace("Request: " + request);

    HttpResponse response;
    if (is100ContinueExpected(request)) {
      response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
    } else {
      HttpRequestContext.setRequest(request);
      try {
        response = requestProcessor.process(request);
      } finally {
        HttpRequestContext.clear();
      }
    }

    if (isKeepAlive(request)) {
      response.headers().set(CONNECTION, KEEP_ALIVE);
      context.writeAndFlush(response);
    } else {
      context.writeAndFlush(response).addListener(CLOSE);
    }

    logger.trace("Response: " + response);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
    logger.error("Caught an exception:", cause);
    context.close();
  }

}
