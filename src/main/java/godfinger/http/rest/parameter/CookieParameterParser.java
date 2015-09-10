package godfinger.http.rest.parameter;

import java.util.Collections;
import java.util.Set;

import godfinger.http.rest.InvocationContext;
import godfinger.http.rest.exception.ParameterNotFoundException;
import godfinger.util.cast.LexicalCaster;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

public class CookieParameterParser implements ResourceArgumentParser {

  private final String name;
  private final LexicalCaster<?> lexicalCaster;
  private final boolean optional;
  private final String defaultValue;

  public CookieParameterParser(String name, LexicalCaster<?> lexicalCaster, boolean optional, String defaultValue) {
    this.name = name;
    this.lexicalCaster = lexicalCaster;
    this.optional = optional;
    this.defaultValue = defaultValue;
  }

  @Override
  public Object parse(InvocationContext context) {
    @SuppressWarnings("unchecked")
    Set<Cookie> cookies = (Set<Cookie>) context.get(this);
    if (cookies == null) {
      FullHttpRequest request = context.getRequest();
      String header = request.headers().get("Cookie");
      cookies = header == null ? Collections.<Cookie>emptySet() : ServerCookieDecoder.STRICT.decode(header);
      context.set(this, cookies);
    }

    for (Cookie cookie : cookies) {
      if (name.equals(cookie.name())) {
        return lexicalCaster.cast(cookie.value());
      }
    }

    if (optional) {
      return defaultValue;
    }

    throw new ParameterNotFoundException("@Cookie", name);
  }

}
