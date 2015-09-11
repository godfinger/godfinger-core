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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

  private final HttpRequestProcessor requestProcessor;

  public HttpServerInitializer(HttpRequestProcessor requestProcessor) {
    this.requestProcessor = requestProcessor;
  }

  @Override
  public void initChannel(SocketChannel channel) {
//    CorsConfig corsConfig = CorsConfig
//            .withAnyOrigin()
//            .allowCredentials()
//            .allowedRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
//            .allowedRequestHeaders("accept", "content-type", "session-id")
//            .build();

    ChannelPipeline pipeline = channel.pipeline();
    pipeline.addLast(new HttpServerCodec());
    pipeline.addLast(new FaviconHandler());
//    pipeline.addLast(new CorsHandler(corsConfig));
    pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
    pipeline.addLast(new HttpRequestHandler(requestProcessor));
  }

}
