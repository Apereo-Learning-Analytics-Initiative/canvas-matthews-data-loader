package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIntegerFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "user_id", "address", "type", "position",
        "workflow_state", "created_at", "updated_at"})
public class CanvasCommunicationChannelDimension implements ReadableCanvasDumpArtifact<CanvasCommunicationChannelDimension.Types>  {

  public enum Types {
    communication_channel_dim
  }
  
  /**
   * Columns

      Name  Type  Description
      id  bigint  Unique surrogate ID for the communication channel.
      canvas_id bigint  Primary key for this communication channel in the communication_channel table.
      user_id bigint  Foreign key to the user that owns this communication channel.
      address varchar Address, or path, of the communication channel. Set to 'NULL' for push notifications.
      type  varchar Denotes the type of the path. Possible values are 'email', 'facebook', 'push' (device push notifications), 'sms' and 'twitter'. Defaults to 'email'.
      position  integer Position of this communication channel relative to the user's other channels when they are ordered.
      workflow_state  varchar Current state of the communication channel. Possible values are 'unconfirmed' and 'active'.
      created_at  timestamp Date/Time when the quiz was created.
      updated_at  timestamp Date/Time when the quiz was last updated.

   * 
   */
  
  @JsonProperty("id")
  private String id;
  
  @JsonProperty("canvas_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> canvasId;

  @JsonProperty("user_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> userId;

  @JsonProperty("address")
  private String address;

  @JsonProperty("type")
  private String type;

  @JsonProperty("position")
  @JsonDeserialize(using = NullableIntegerFieldDeserializer.class)
  private Optional<Integer> position;
  
  @JsonProperty("workflow_state")
  private String workflowState;

  @JsonProperty("created_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant createdAt;

  @JsonProperty("updated_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant updatedAt;

}
