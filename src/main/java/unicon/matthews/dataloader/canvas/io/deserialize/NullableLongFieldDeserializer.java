package unicon.matthews.dataloader.canvas.io.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Optional;

import static unicon.matthews.dataloader.canvas.io.deserialize.CanvasDataFieldValueOptions.NULL;

/**
 * Deserializes a field of field of type {@link Long} which may be NULL, which is represented as <em>\N</em> in
 * the Canvas data dumps.
 * <p>This deserializer returns an <code>Optional</code> to better designate that the field value is optional.</p>
 */
public class NullableLongFieldDeserializer extends JsonDeserializer<Optional<Long>> {

    @Override
    public Optional<Long> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String fieldValueText = jsonParser.getText();
        Long fieldValue = null;
        if (!NULL.getFieldValue().equalsIgnoreCase(fieldValueText)) {
            fieldValue = Long.valueOf(fieldValueText);
        }
        return Optional.ofNullable(fieldValue);
    }
}
