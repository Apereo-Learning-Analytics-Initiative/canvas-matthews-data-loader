package unicon.matthews.dataloader.canvas.io.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;

/**
 * Deserializes a Unix Timestamp in milliseconds since the epoch into an <code>Instant</code>
 *
 * <p>Derived from a couple locations
 * <a href="http://stackoverflow.com/a/20638114">http://stackoverflow.com/a/20638114</a> and
 * <a href="http://stackoverflow.com/a/27952473">http://stackoverflow.com/a/27952473</a></p>
 */
public class EpochMillisecondsDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Instant.ofEpochMilli(Long.parseLong(jsonParser.getText()));
    }
}
