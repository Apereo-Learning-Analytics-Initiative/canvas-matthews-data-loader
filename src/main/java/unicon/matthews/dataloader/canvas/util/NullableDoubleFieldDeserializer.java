package unicon.matthews.dataloader.canvas.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Optional;

import static unicon.matthews.dataloader.canvas.util.CanvasDataFieldValueOptions.NULL;

/**
 * Deserializes a field of field of type Double which may be NULL, which is represented as <em>\N</em> in the Canvas
 * data dumps.
 * <p>This deserializer returns an <code>Optional</code> to better designate that the field value is optional.</p>
 */
public class NullableDoubleFieldDeserializer extends JsonDeserializer<Optional<Double>> {

    @Override
    public Optional<Double> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String fieldValueText = jsonParser.getText();
        Double fieldValue = null;
        if (!NULL.getFieldValue().equalsIgnoreCase(fieldValueText)) {
          fieldValue = Double.valueOf(fieldValueText);
        }
        return Optional.ofNullable(fieldValue);
    }
}
