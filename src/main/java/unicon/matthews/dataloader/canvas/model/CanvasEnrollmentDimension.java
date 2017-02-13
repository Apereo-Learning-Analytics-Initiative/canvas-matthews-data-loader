package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableBooleanFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "root_account_id", "course_section_id", "role_id", "type",
        "workflow_state", "created_at", "updated_at", "start_at",
        "end_at", "completed_at", "self_enrolled", "sis_source_id", "course_id", "user_id"})
public class CanvasEnrollmentDimension implements ReadableCanvasDumpArtifact<CanvasEnrollmentDimension.Types> {
  public enum Types {
    enrollment_dim
  }

  /**
   * https://portal.inshosteddata.com/docs#enrollment_dim
   * 
   * 
   * Columns

      Name  Type  Description
      id  bigint  Unique surrogate id for the enrollment.
      canvas_id bigint  Primary key for this record in the Canvas enrollments table
      root_account_id bigint  Root account id associated with this enrollment
      course_section_id bigint  Foreign key to the course section for this enrollment
      role_id bigint  Foreign key to the role of the person enrolled in the course
      type  varchar Enrollment type: TaEnrollment, DesignerEnrollment, StudentEnrollment, TeacherEnrollment, StudentViewEnrollment, ObserverEnrollment
      workflow_state  varchar Workflow state for enrollment: active, completed, rejected, deleted, invited, creation_pending
      created_at  timestamp Timestamp for when this section was entered into the system.
      updated_at  timestamp Timestamp for when the last time the section was updated
      start_at  timestamp Enrollment start date
      end_at  timestamp Enrollment end date
      completed_at  timestamp Enrollment completed date
      self_enrolled boolean Enrollment was created via self-enrollment
      sis_source_id varchar (Deprecated) No longer used in Canvas.
      course_id bigint  Foreign key to course for this enrollment
      user_id bigint  Foreign key to user for the enrollment
   * 
   * 
   */

  @JsonProperty("id")
  private String id;
  
  @JsonProperty("canvas_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> canvasId;

  @JsonProperty("root_account_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> rootAccountId;

  @JsonProperty("course_section_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> courseSectionId;
  
  @JsonProperty("role_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> roleId;

  @JsonProperty("type")
  private String type;
  
  @JsonProperty("workflow_state")
  private String workflowState;

  @JsonProperty("created_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant createdAt;
  
  @JsonProperty("updated_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant updatedAt;

  @JsonProperty("start_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> startAt;
  
  @JsonProperty("end_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> endAt;
  
  @JsonProperty("completed_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> completedAt;

  @JsonProperty("self_enrolled")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> selfEnrolled;
  
  @JsonProperty("sis_source_id")
  private String sisSourceId;

  @JsonProperty("course_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> courseId;
  
  @JsonProperty("user_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> userId;

}
