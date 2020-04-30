package de.juserv.poe.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom JsonDeserializer to map an flat Arrays to an Object by using {@link JsonProperty#index()}.
 * The Type of the Object needs to be set in JAVA_TYPE, tu support reflection.
 */
public class IndexDeserializer extends JsonDeserializer<List<Object>> {

    /** ClassType to bass directly trough Attributes to support generic mappings */
    public static final String JAVA_TYPE = "type";

    @Override
    public List<Object> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonNode node = p.readValueAsTree();
        Class<?> targetClass = (Class<?>) context.getAttribute(JAVA_TYPE);
        ArrayList<Object> list = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            try {
                Object o = targetClass.newInstance();
                for (Field f : targetClass.getDeclaredFields()) {
                    JsonProperty property = f.getDeclaredAnnotation(JsonProperty.class);
                    if (property != null) {
                        f.setAccessible(true);
                        if (f.getType().isAssignableFrom(String.class)) {
                            f.set(o, node.get(i).get(property.index()).asText());
                        } else if (f.getType().isAssignableFrom(Long.class)) {
                            f.set(o, node.get(i).get(property.index()).asLong());
                        } else if (f.getType().isAssignableFrom(Boolean.class)) {
                            f.set(o, node.get(i).get(property.index()).asBoolean());
                        }
                    } else if ("id".equals(f.getName())) {
                        f.setAccessible(true);
                        f.set(o, i);
                    }
                }
                list.add(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
