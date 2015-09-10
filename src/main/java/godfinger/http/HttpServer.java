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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HttpServer {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
  private final EventLoopGroup workerGroup = new NioEventLoopGroup();
  private final ServerBootstrap bootstrap = new ServerBootstrap();

  private final int port;

  private Channel channel;

  public HttpServer(int port, HttpRequestProcessor requestProcessor) {
    this.port = port;
    bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
    bootstrap.group(bossGroup, workerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.handler(new LoggingHandler(LogLevel.INFO));
    bootstrap.childHandler(new HttpServerInitializer(requestProcessor));
  }

  public void start() throws InterruptedException {
    channel = bootstrap.bind(port).sync().channel();
    logger.info("HTTP Server at port " + port + " has been started.");
  }

  public void gracefulStop(long gracePeriod, TimeUnit timeUnit) throws InterruptedException {
    long gracePeriodMillis;
    long startTimeMillis = System.currentTimeMillis();

    channel.close().sync();

    gracePeriodMillis = timeUnit.toMillis(gracePeriod) - (System.currentTimeMillis() - startTimeMillis);
    if (gracePeriodMillis < 0) {
      gracePeriodMillis = 0;
    }
    bossGroup.shutdownGracefully(0L, gracePeriodMillis, TimeUnit.MILLISECONDS);

    gracePeriodMillis = timeUnit.toMillis(gracePeriod) - (System.currentTimeMillis() - startTimeMillis);
    if (gracePeriodMillis < 0) {
      gracePeriodMillis = 0;
    }
    workerGroup.shutdownGracefully(0L, gracePeriodMillis, TimeUnit.MILLISECONDS);

    logger.info("HTTP Server at port " + port + " has been stopped.");
  }

}
