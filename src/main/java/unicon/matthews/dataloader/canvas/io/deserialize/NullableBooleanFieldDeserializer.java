package unicon.matthews.dataloader.canvas.io.deserialize;

import static unicon.matthews.dataloader.canvas.io.deserialize.CanvasDataFieldValueOptions.NULL;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class NullableBooleanFieldDeserializer extends JsonDeserializer<Optional<Boolean>> {

  @Override
  public Optional<Boolean> deserialize(JsonParser jsonParser, DeserializationContext arg1) throws IOException, JsonProcessingException {
    String fieldValueText = jsonParser.getText();
    Boolean fieldValue = null;
    if (!NULL.getFieldValue().equalsIgnoreCase(fieldValueText)) {
      fieldValue = Boolean.valueOf(fieldValueText);
    }
    return Optional.ofNullable(fieldValue);
  }

}
