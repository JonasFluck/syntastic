package org.example.helper;

import java.util.Map;

public class AttributeUtils {
    @SuppressWarnings("unchecked")
    public static <T> T getAttributeAs(Map<String, Object> attributes, String key, Class<T> clazz) {
        Object obj = attributes.get(key);
        if (obj == null) {
            return null;
        }
        if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        throw new ClassCastException("Object under key '" + key + "' is not of type " + clazz.getName());
    }
}

