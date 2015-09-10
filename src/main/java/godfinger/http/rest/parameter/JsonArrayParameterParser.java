package godfinger.http.rest.parameter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import godfinger.http.rest.InvocationContext;
import godfinger.http.rest.exception.ParameterNotFoundException;
import io.netty.handler.codec.http.FullHttpRequest;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.util.CharsetUtil.UTF_8;

public class JsonArrayParameterParser implements ResourceArgumentParser {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final Class<?> elementType;
  private final String name;
  private final Class<?> collectionType;
  private final boolean optional;
  private final String defaultValue;

  public JsonArrayParameterParser(Class<?> elementType, String name, Class<?> collectionType, boolean optional, String defaultValue) {
    this.elementType = elementType;
    this.name = name;
    this.collectionType = collectionType;
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
        throw new ParameterNotFoundException("@JsonArray", name);
      }

      String content = request.content().toString(UTF_8);
      rootNode = objectMapper.readTree(content);
      context.set(this, rootNode);
    }

    if (!rootNode.isMissingNode()) {
      JsonNode targetNode = name.isEmpty() ? rootNode : rootNode.path(name);
      if (!targetNode.isMissingNode() && targetNode.isArray()) {
        List<Object> result = new ArrayList<>(targetNode.size());
        for (JsonNode elementNode : targetNode) {
          Object element = objectMapper.treeToValue(elementNode, elementType);
          result.add(element);
        }
        return result;
      }
    }

    if (optional) {
      if (defaultValue == null) {
        return null;
      }
      return objectMapper.readValue(defaultValue, collectionType);
    }

    throw new ParameterNotFoundException("@JsonArray", name);
  }

}
