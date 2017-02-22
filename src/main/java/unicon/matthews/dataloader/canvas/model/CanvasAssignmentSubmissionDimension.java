package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import unicon.matthews.dataloader.canvas.io.deserialize.NullableBooleanFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIntegerFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "body", "url", "grade", "submitted_at", "submission_type",
        "workflow_state", "created_at", "updated_at", "processed",
        "process_attempts", "grade_matches_current_submission", "published_grade",
        "graded_at", "has_rubric_assessment", "attempt", "has_admin_comment",
        "assignment_id", "excused", "graded_anonymously", "grader_id",
        "group_id", "quiz_submission_id", "user_id", "grade_state"})
public class CanvasAssignmentSubmissionDimension implements ReadableCanvasDumpArtifact<CanvasAssignmentSubmissionDimension.Types> {

  public enum Types {
    submission_dim
  }
  
  /**
   * 
   * Columns

      Name  Type  Description
      id  bigint  Unique surrogate ID for the submission.
      canvas_id bigint  Primary key of this record in the Canvas submissions table.
      body  text  Text content for the submission.
      url varchar URL content for the submission.
      grade varchar Letter grade mapped from the score by the grading scheme.
      submitted_at  timestamp Timestamp of when the submission was submitted.
      submission_type enum  Type of submission. Possible values are 'discussion_topic', 'external_tool', 'media_recording', 'online_file_upload', 'online_quiz', 'online_text_entry', 'online_upload' and 'online_url'.
      workflow_state  enum  Workflow state for submission lifetime values. Possible values are 'graded', 'pending_review', 'submitted' and 'unsubmitted'.
      created_at  timestamp Timestamp of when the submission was created.
      updated_at  timestamp Timestamp of when the submission was last updated.
      processed boolean Valid only when there is a file/attachment associated with the submission. By default, this attribute is set to 'false' when making the assignment submission. When a submission has a file/attachment associated with it, upon submitting the assignment a snapshot is saved and its' value is set to 'true'. Defaults to 'NULL'.
      process_attempts  int (Deprecated) No longer used in Canvas.
      grade_matches_current_submission  boolean Valid only when a score has been assigned to a submission. This is set to 'false' if a student makes a new submission to an already graded assignment. This is done to indicate that the current grade given by the teacher is not for the most recent submission by the student. It is set to 'true' if a score has been given and there is no new submission. Defaults to 'NULL'.
      published_grade varchar Valid only for a graded submission. The values are strings that reflect the grading type used. For example, a scoring method of 'points' will show '4' if given a '4' out of '5', and a scoring method of 'letter grade' will show 'B' for the same score (assuming a grading scale where 80-90% is a 'B'). Defaults to 'NULL'.
      graded_at timestamp Timestamp of when the submission was graded.
      has_rubric_assessment boolean Valid only for a graded submission. Its' value is set to 'true' if the submission is associated with a rubric that has been assessed for at least one student, otherwise is set to 'false'. Defaults to 'NULL'.
      attempt int The number of attempts made including this one.
      has_admin_comment boolean (Deprecated) No longer used in Canvas.
      assignment_id bigint  Foreign key to assignment dimension.
      excused enum  Denotes if this submission is excused or not. Possible values are 'excused_submission' and 'regular_submission'.
      graded_anonymously  enum  Denotes how the grading has been performed. Possible values are 'graded_anonymously' and 'not_graded_anonymously'.
      grader_id bigint  Foreign key to the user dimension of user who graded the assignment.
      group_id  bigint  Foreign key to the group_dim table.
      quiz_submission_id  bigint  Foreign key to the quiz_submission_dim table.
      user_id bigint  Foreign key to the user_dim table.
      grade_state enum  Denotes the current state of the grade. Possible values are 'auto_graded', 'human_graded' and 'not_graded'.

   * 
   * 
   */
  
  @JsonProperty("id")
  private Long id;

  @JsonProperty("canvas_id")
  private Long canvasId;
  
  @JsonProperty("body")
  private String body;

  @JsonProperty("url")
  private String url;
  
  @JsonProperty("grade")
  private String grade;
  
  @JsonProperty("submitted_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> submittedAt;
  
  @JsonProperty("submission_type")
  private String submissionType;
  
  @JsonProperty("workflow_state")
  private String workflowState;
  
  @JsonProperty("created_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> createdAt;
  
  @JsonProperty("updated_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> updatedAt;
 
  @JsonProperty("processed")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> processed;

  @JsonProperty("process_attempts")
  @JsonDeserialize(using = NullableIntegerFieldDeserializer.class)
  private Optional<Integer> processAttempts;
  
  @JsonProperty("grade_matches_current_submission")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> gradeMatchesCurrentSubmission;

  @JsonProperty("published_grade")
  private String publishedGrade;
  
  @JsonProperty("graded_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> gradedAt;

  @JsonProperty("has_rubric_assessment")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> hasRubricAssessment;

  @JsonProperty("attempt")
  @JsonDeserialize(using = NullableIntegerFieldDeserializer.class)
  private Optional<Integer> attempt;

  @JsonProperty("has_admin_comment")
  @JsonDeserialize(using = NullableBooleanFieldDeserializer.class)
  private Optional<Boolean> hasAdminComment;

  @JsonProperty("assignment_id")
  private Long assignmentId;

  @JsonProperty("excused")
  private String excused;

  @JsonProperty("graded_anonymously")
  private String gradedAnonymously;

  @JsonProperty("grader_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> graderId;

  @JsonProperty("group_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> groupId;

  @JsonProperty("quiz_submission_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> quizSubmissionId;

  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("grade_state")
  private String gradeState;

}
