package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

public class GaodeCityCodeDeserializer extends JsonDeserializer<String> {

    public GaodeCityCodeDeserializer() {
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node instanceof TextNode) {
            return node.asText();
        }
        if (node instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) node;
            if (arrayNode.isEmpty()) {
                return null;
            } else {
                try {
                    throw new Exception("not empty array node!");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        throw new IllegalArgumentException("illegal node: " + node);
    }
}