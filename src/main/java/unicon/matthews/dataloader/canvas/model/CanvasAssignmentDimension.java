package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableBooleanFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableDoubleFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIntegerFieldDeserializer;
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
@JsonPropertyOrder({ 
  "id", 
  "canvas_id", 
  "course_id", 
  "title", 
  "description", 
  "due_at",
  "unlock_at", 
  "lock_at", 
  "points_possible", 
  "grading_type",
  "submission_types", 
  "workflow_state", 
  "created_at", 
  "updated_at", 
  "peer_review_count", 
  "peer_reviews_due_at",
  "peer_reviews_assigned", 
  "peer_reviews", 
  "automatic_peer_reviews", 
  "all_day", 
  "all_day_date", 
  "could_be_locked",
  "grade_group_students_individually", 
  "anonymous_peer_reviews",
  "muted", 
  //"random_unknown_id", // in practice there is an id in this column but it is not in the docs
  "assignment_group_id",
  "position",
  "visibility"
  })
public class CanvasAssignmentDimension implements ReadableCanvasDumpArtifact<CanvasAssignmentDimension.Types> {
  public enum Types {
    assignment_dim
  }

  /**
   * https://portal.inshosteddata.com/docs#assignment_dim
   * 
   * Columns

      Name  Type  Description
      id  bigint  Unique surrogate ID for the assignment.
      canvas_id bigint  Primary key for this record in the Canvas assignments table.
      course_id bigint  Foreign key to the course associated with this assignment
      title varchar Title of the assignment
      description text  Long description of the assignment
      due_at  timestamp Timestamp for when the assignment is due
      unlock_at timestamp Timestamp for when the assignment is unlocked or visible to the user
      lock_at timestamp Timestamp for when the assignment is locked
      points_possible double precision  Total points possible for the assignment
      grading_type  varchar Describes how the assignment will be graded (gpa_scale, pass_fail, percent, points, not_graded, letter_grade)
      submission_types  varchar Comma separated list of valid methods for submitting the assignment (online_url, media_recording, online_upload, online_quiz, external_tool, online_text_entry, online_file_upload)
      workflow_state  varchar Current workflow state of the assignment. Possible values are unpublished, published and deleted
      created_at  timestamp Timestamp of the first time the assignment was entered into the system
      updated_at  timestamp Timestamp of the last time the assignment was updated
      peer_review_count int The number of pears to assign for review if using algorithmic assignment
      peer_reviews_due_at timestamp Timestamp for when peer reviews should be completed
      peer_reviews_assigned boolean True if all peer reviews have been assigned
      peer_reviews  boolean True if peer reviews are enabled for this assignment
      automatic_peer_reviews  boolean True if peer reviews are assigned algorithmically (vs. letting the instructor make manual assignments)
      all_day boolean True if A specific time for when the assignment is due was not given. The effective due time will be 11:59pm.
      all_day_date  date  The date version of the due date if the all_day flag is true.
      could_be_locked boolean True if the assignment is under a module that can be locked
      grade_group_students_individually boolean True if students who submit work as a group will each receive individual grades (vs one grade that is copied to all group members)
      anonymous_peer_reviews  boolean (currently unimplemented, do not use)
      muted boolean Student cannot see grades left on the assignment.
      assignment_group_id bigint  Foreign key to the assignment group dimension table.
   */

  
  @JsonProperty("id")
  private String id;
  
  @JsonProperty("canvas_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> canvasId;
    
  @JsonProperty("course_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> courseId;
  
  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;
  
  @JsonProperty("due_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> dueAt;

  @JsonProperty("unlock_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> unlockAt;

  @JsonProperty("lock_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> lockAt;

  @JsonProperty("points_possible")
  @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
  private Optional<Double> pointsPossible;
  
  @JsonProperty("grading_type")
  private String gradingType;

  @JsonProperty("submission_types")
  private String submissionTypes;

  @JsonProperty("workflow_state")
  private String workflowState;

  @JsonProperty("created_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant createdAt;
  
  @JsonProperty("updated_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant updatedAt;

  @JsonProperty("peer_review_count")
  @JsonDeserialize(using = NullableIntegerFieldDeserializer.class)
  private Optional<Boolean> peerReviewCount;
  
  @JsonProperty("peer_reviews_due_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> peerReviewsDueAt;

  @JsonProperty("peer_reviews")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> peerReviews;
  
  @JsonProperty("automatic_peer_reviews")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> automaticPeerReviews;
  
  @JsonProperty("peer_reviews_assigned")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> peerReviewsAssigned;
  
  @JsonProperty("all_day")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> allDay;
  
  @JsonProperty("all_day_date")
  //@JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private String allDayDate;
  
  @JsonProperty("could_be_locked")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> couldBeLocked;
  
  @JsonProperty("grade_group_students_individually")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> gradeGroupStudentsIndividually;

  @JsonProperty("anonymous_peer_reviews")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> anonymousPeerReviews;

  @JsonProperty("muted")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> muted;
  
  @JsonProperty("random_unknown_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> randomUnknownId;

  @JsonProperty("assignment_group_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> assignmentGroupId;
  
  @JsonProperty("position")
  private String position;
  
  @JsonProperty("visibility")
  private String visibility;

}
