package godfinger.http.rest.parameter;

import java.util.List;

import godfinger.http.rest.InvocationContext;
import godfinger.http.rest.exception.ParameterNotFoundException;
import godfinger.util.cast.LexicalCaster;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class QueryParameterParser implements ResourceArgumentParser {

  private final String name;
  private final LexicalCaster<?> lexicalCaster;
  private final boolean optional;
  private final String defaultValue;

  public QueryParameterParser(String name, LexicalCaster<?> lexicalCaster, boolean optional, String defaultValue) {
    this.name = name;
    this.lexicalCaster = lexicalCaster;
    this.optional = optional;
    this.defaultValue = defaultValue;
  }

  @Override
  public Object parse(InvocationContext context) {
    QueryStringDecoder decoder = (QueryStringDecoder) context.get(this);
    if (decoder == null) {
      FullHttpRequest request = context.getRequest();
      decoder = new QueryStringDecoder(request.getUri());
      context.set(this, decoder);
    }

    List<String> values = decoder.parameters().get(name);
    if (values != null) {
      return lexicalCaster.cast(values.get(0));
    }

    if (optional) {
      return lexicalCaster.cast(defaultValue);
    }

    throw new ParameterNotFoundException("@Query", name);
  }

}
