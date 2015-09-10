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

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestContext {

  private static final ThreadLocal<Map<String, Object>> threadLocalMap = new ThreadLocal<Map<String, Object>>() {
    protected Map<String, Object> initialValue() {
      return new HashMap<>();
    }
  };

  private static final ThreadLocal<FullHttpRequest> threadLocalRequest = new ThreadLocal<>();

  public static FullHttpRequest getRequest() {
    return threadLocalRequest.get();
  }

  public static Object get(String key) {
    return threadLocalMap.get().get(key);
  }

  public static void set(String key, Object value) {
    threadLocalMap.get().put(key, value);
  }

  static void setRequest(FullHttpRequest request) {
    threadLocalRequest.set(request);
  }

  static void clear() {
    threadLocalMap.remove();
    threadLocalRequest.remove();
  }

}
