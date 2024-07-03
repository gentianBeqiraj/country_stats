package ch.bbw.util.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Custom deserializer to convert JSON strings to numeric types.
 * Supports conversion to Byte, Integer, and Long.
 *
 * @param <T> the type of Number to deserialize to
 */
public class StringToNumberDeserializer<T extends Number> extends JsonDeserializer<T> {
  private final Class<T> targetType;

  /**
   * Constructor for StringToNumberDeserializer.
   *
   * @param targetType the class of the target numeric type
   */
  public StringToNumberDeserializer(Class<T> targetType) {
    this.targetType = targetType;
  }

  /**
   * Deserializes a JSON string into a numeric type.
   *
   * @param p     the JsonParser used to read the JSON content
   * @param ctxt  the DeserializationContext
   * @return the deserialized number of type T, or null if the string cannot be parsed
   * @throws IOException if an I/O error occurs
   */
  @Override
  @SuppressWarnings("unchecked")
  public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String value = p.getText();

    try {
      double doubleValue = Double.parseDouble(value);

      if (targetType == Byte.class) {
        return (T) Byte.valueOf((byte) Math.round(doubleValue));
      } else if (targetType == Integer.class) {
        return (T) Integer.valueOf((int) Math.round(doubleValue));
      } else if (targetType == Long.class) {
        return (T) Long.valueOf(Math.round(doubleValue));
      } else {
        throw new UnsupportedOperationException("Unsupported target type: " + targetType);
      }
    } catch (NumberFormatException e) {
      return null;
    }
  }
}

