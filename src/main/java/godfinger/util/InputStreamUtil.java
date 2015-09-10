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
package godfinger.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class InputStreamUtil {

  public static InputStream openInputStream(String resourceString, ClassLoader classLoader) throws IOException {
    int indexOfColon = resourceString.indexOf(':');
    String prefix = resourceString.substring(0, indexOfColon);
    if ("classpath".equalsIgnoreCase(prefix)) {
      String path = resourceString.substring(indexOfColon + 1);
      return classLoader.getResourceAsStream(path);
    }
    return new URL(resourceString).openStream();
  }

  public static String toString(InputStream is) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    }
    return sb.toString();
  }

}
