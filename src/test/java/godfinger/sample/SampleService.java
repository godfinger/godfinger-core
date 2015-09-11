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
package godfinger.sample;

import org.springframework.stereotype.Service;

import godfinger.http.rest.annotation.Export;
import godfinger.http.rest.annotation.HttpDelete;
import godfinger.http.rest.annotation.HttpGet;
import godfinger.http.rest.annotation.HttpPost;
import godfinger.http.rest.annotation.HttpPut;
import godfinger.http.rest.annotation.Query;

@Export("sample")
@Service
public class SampleService {

  private String helloTo = "World";

  @HttpPost("echo")
  public String echo(@Query("message") String message) throws Exception {
    return message;
  }

  @HttpGet("hello")
  public String hello() throws Exception {
    return "Hello, " + helloTo + "!";
  }

  @HttpPut("hello")
  public void helloTo(String helloTo) throws Exception {
    this.helloTo = helloTo;
  }

  @HttpDelete("hello")
  public void helloTo() throws Exception {
    helloTo = "World";
  }

}