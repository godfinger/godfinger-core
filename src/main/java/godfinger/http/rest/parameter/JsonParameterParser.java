package godfinger.http.rest.parameter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import godfinger.http.rest.InvocationContext;
import godfinger.http.rest.exception.ParameterNotFoundException;
import io.netty.handler.codec.http.FullHttpRequest;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.util.CharsetUtil.UTF_8;

public class JsonParameterParser implements ResourceArgumentParser {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final String name;
  private final Class<?> type;
  private final boolean optional;
  private final String defaultValue;

  public JsonParameterParser(String name, Class<?> type, boolean optional, String defaultValue) {
    this.name = name;
    this.type = type;
    this.optional = optional;
    this.defaultValue = defaultValue;
  }

  @Override
  public Object parse(InvocationContext context) throws Exception {
    JsonNode rootNode = (JsonNode) context.get(this);
    if (rootNode == null) {
      FullHttpRequest request = context.getRequest();
      String contentType = request.headers().get(CONTENT_TYPE);
      if (contentType == null || !contentType.startsWith("application/json")) {
        throw new ParameterNotFoundException("@Json", name);
      }

      String content = request.content().toString(UTF_8);
      rootNode = objectMapper.readTree(content);
      context.set(this, rootNode);
    }

    if (!rootNode.isMissingNode()) {
      JsonNode targetNode = rootNode.path(name);
      if (!targetNode.isMissingNode()) {
        if (targetNode.isArray()) {

        }
        return objectMapper.treeToValue(targetNode, type);
      }
    }

    if (optional) {
      if (defaultValue == null) {
        return null;
      }
      return objectMapper.readValue(defaultValue, type);
    }

    throw new ParameterNotFoundException("@Json", name);
  }

}
