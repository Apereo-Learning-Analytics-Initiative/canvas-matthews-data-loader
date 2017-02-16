package unicon.matthews.dataloader.canvas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import java.time.Instant;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "message", "workflow_state", "created_at", "updated_at", "deleted_at", "depth" })
public class CanvasDiscussionForumEntryDimension implements ReadableCanvasDumpArtifact<CanvasDiscussionForumEntryDimension.Types> {

  public enum Types {
      discussion_entry_dim
  }

  /**
   * https://portal.inshosteddata.com/docs#discussion_entry_dim
   *
   Columns
   Name	Type	Description
   id	bigint	Unique surrogate id for the discussion entry.
   canvas_id	bigint	Primary key for this record in the Canvas discussion_entries table
   message	text	Full text of the entry's message
   workflow_state	varchar	Workflow state for discussion message (values: deleted, active)
   created_at	timestamp	Timestamp when the discussion entry was created.
   updated_at	timestamp	Timestamp when the discussion entry was updated.
   deleted_at	timestamp	Timestamp when the discussion entry was deleted.
   depth	int	Reply depth for this entry
   */
  
  @JsonProperty("id")
  private Long id;
  
  @JsonProperty("canvas_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> canvasId;
  
  @JsonProperty("message")
  private String message;
  
  @JsonProperty("workflow_state")
  private String workflowState;
  
  @JsonProperty("created_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant createdAt;
  
  @JsonProperty("updated_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> updatedAt;
  
  @JsonProperty("deleted_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> deletedAt;

  @JsonProperty("depth")
  private int depth;

}
