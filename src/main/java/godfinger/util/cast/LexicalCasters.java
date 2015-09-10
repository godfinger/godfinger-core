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
package godfinger.util.cast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LexicalCasters {

  @SuppressWarnings("serial")
  private static final Map<Class<?>, LexicalCaster<?>> LEXICAL_CASTERS = new HashMap<Class<?>, LexicalCaster<?>>() {
    {
      put(byte.class, new IntegerCaster());
      put(Byte.class, get(int.class));
      put(char.class, new CharacterCaster());
      put(Character.class, get(char.class));
      put(int.class, new IntegerCaster());
      put(Integer.class, get(int.class));
      put(short.class, new ShortCaster());
      put(Short.class, get(short.class));
      put(long.class, new LongCaster());
      put(Long.class, get(long.class));
      put(double.class, new DoubleCaster());
      put(Double.class, get(double.class));
      put(String.class, new StringCaster());
      put(Date.class, new DateCaster());
    }
  };

  private LexicalCasters() {
  }

  @SuppressWarnings("unchecked")
  public static <T> LexicalCaster<T> get(Class<T> type) {
    return (LexicalCaster<T>) LEXICAL_CASTERS.get(type);
  }

}
