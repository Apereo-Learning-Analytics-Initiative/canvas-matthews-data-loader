package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableBooleanFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
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
@JsonPropertyOrder({ "id", "canvas_id", "name", "course_id", "enrollment_term_id", "default_section",
        "accepting_enrollments", "can_manually_enroll", "start_at", "end_at",
        "created_at", "updated_at", "workflow_state", "restrict_enrollments_to_section_dates", "nonxlist_course_id", "sis_source_id"})
public class CanvasCourseSectionDimension implements ReadableCanvasDumpArtifact<CanvasCourseSectionDimension.Types> {

  public enum Types {
    course_section_dim
  }
  
  /**
   * https://portal.inshosteddata.com/docs#course_section_dim
   * 
   * Columns

      Name  Type  Description
      id  bigint  Unique surrogate id for the course section.
      canvas_id bigint  Primary key for this record in the Canvas course_sections table.
      name  varchar Name of the section
      course_id bigint  Foreign key to the associated course
      enrollment_term_id  bigint  Foreign key to the associated enrollment term
      default_section boolean True if this is the default section
      accepting_enrollments boolean True if this section is open for enrollment
      can_manually_enroll boolean Deprecated
      start_at  timestamp Section start date
      end_at  timestamp Section end date
      created_at  timestamp Timestamp for when this section was entered into the system.
      updated_at  timestamp Timestamp for when the last time the section was updated
      workflow_state  varchar Life-cycle state for section. (active, deleted)
      restrict_enrollments_to_section_dates boolean True when "Users can only participate in the course between these dates" is checked
      nonxlist_course_id  bigint  The course id for the original course if this course has been cross listed
      sis_source_id varchar Id for the correlated record for the section in the SIS (assuming SIS integration has been properly configured)
   */

  
  @JsonProperty("id")
  private String id;
  
  @JsonProperty("canvas_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> canvasId;
  
  @JsonProperty("name")
  private String name;
  
  @JsonProperty("course_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> courseId;
  
  @JsonProperty("enrollment_term_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> enrollmentTermId;
  
  @JsonProperty("default_section")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> defaultSection;
  
  @JsonProperty("accepting_enrollments")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> acceptingEnrollments;
  
  @JsonProperty("can_manually_enroll")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> canManuallyEnroll;
  
  @JsonProperty("start_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> startAt;
  
  @JsonProperty("end_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> endAt;
  
  @JsonProperty("created_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant createdAt;
  
  @JsonProperty("updated_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant updatedAt;
  
  @JsonProperty("workflow_state")
  private String workflowState;
  
  @JsonProperty("restrict_enrollments_to_section_dates")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> restrictEnrollmentsToSectionDates;
  
  @JsonProperty("nonxlist_course_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> nonxlistCourseId;
  
  @JsonProperty("sis_source_id")
  private String sisSourceId;
  
  

}
