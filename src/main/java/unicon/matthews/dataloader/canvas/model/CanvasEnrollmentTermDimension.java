package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "root_account_id", "name", "date_start", "date_end",
  "sis_source_id"})
public class CanvasEnrollmentTermDimension implements ReadableCanvasDumpArtifact<CanvasEnrollmentTermDimension.Types> {

  public enum Types {
    enrollment_term_dim
  }
  
  /**
   * Name Type  Description
    id  bigint  Unique surrogate id for the enrollment term.
    canvas_id bigint  Primary key for this record in the Canvas enrollments table.
    root_account_id bigint  Foreign key to the root account for this enrollment term
    name  varchar Name of the enrollment term
    date_start  timestamp Term start date
    date_end  timestamp Term end date
    sis_source_id varchar Correlated SIS id for this enrollment term (assuming SIS has been configured properly)
   */

  @JsonProperty("id")
  private String id;
  
  @JsonProperty("canvas_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> canvasId;
  
  @JsonProperty("root_account_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> rootAccountId;
  
  @JsonProperty("name")
  private String name;
   
  @JsonProperty("date_start")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> dateStart;
  
  @JsonProperty("date_end")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> dateEnd;
  
  @JsonProperty("sis_source_id")
  private String sisSourceId;

}
