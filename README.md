# Light-weight REST Framework for Microservices

```java
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
```
