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

// Exports the service to http://host:port/sample
@Export("sample")
@Service
public class SampleService {

  private String helloTo = "World";

  // POST method for http://host:port/sample/echo?message={message}
  @HttpPost("echo")
  public String echo(@Query("message") String message) throws Exception {
    return message;
  }

  // GET method for http://host:port/sample/hello
  @HttpGet("hello")
  public String hello() throws Exception {
    return "Hello, " + helloTo + "!";
  }

  // PUT method for http://host:port/sample/hello/{helloTo}
  @HttpPut("hello/{helloTo}")
  public void helloTo(@Path("helloTo") String helloTo) throws Exception {
    this.helloTo = helloTo;
  }

  // DELETE method for http://host:port/sample/hello
  @HttpDelete("hello")
  public void helloTo() throws Exception {
    helloTo = "World";
  }

}
```
## Return Data Format  
The return data format is based on [JSend](http://labs.omniti.com/labs/jsend)
