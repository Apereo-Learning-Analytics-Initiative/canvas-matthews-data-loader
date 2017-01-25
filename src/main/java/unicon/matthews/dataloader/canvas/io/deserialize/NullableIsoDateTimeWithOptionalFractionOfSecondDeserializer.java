package unicon.matthews.dataloader.canvas.io.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static unicon.matthews.dataloader.canvas.io.deserialize.CanvasDataFieldValueOptions.NULL;

/**
 * Deserializes an optional ISO Date Time field which may contain fractions of a second but no time zone, and converts
 * it to an Instant based on UTC. Delegates to the <code>IsoDateTimeWithOptionalFractionOfSecondDeserializer</code> for
 * deserialization if the field is not null.
 * <p>This deserializer returns an <code>Optional</code> to better designate that the field value is optional.</p>
 * @see IsoDateTimeWithOptionalFractionOfSecondDeserializer
 */
public class NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer extends JsonDeserializer<Optional<Instant>>  {

    private IsoDateTimeWithOptionalFractionOfSecondDeserializer delegatedDeserializer =
            new IsoDateTimeWithOptionalFractionOfSecondDeserializer();

    @Override
    public Optional<Instant> deserialize(JsonParser parser, DeserializationContext deserializationContext)
            throws IOException {
        String fieldValueText = parser.getText();
        Instant fieldValue = null;
        if (!NULL.getFieldValue().equalsIgnoreCase(fieldValueText)) {
            fieldValue = delegatedDeserializer.deserialize(parser, deserializationContext);
        }
        return Optional.ofNullable(fieldValue);
    }
}
