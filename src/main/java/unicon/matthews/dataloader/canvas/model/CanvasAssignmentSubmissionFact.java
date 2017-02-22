package unicon.matthews.dataloader.canvas.model;

import java.util.Optional;

import unicon.matthews.dataloader.canvas.io.deserialize.NullableDoubleFieldDeserializer;
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
@JsonPropertyOrder({ "submission_id", "assignment_id", "course_id", "enrollment_term_id", "user_id", "grader_id", "course_account_id",
        "enrollment_rollup_id", "score", "published_score", "what_if_score",
        "submission_comments_count", "account_id", "assignment_group_id",
        "group_id", "quiz_id", "quiz_submission_id", "wiki_id"})
public class CanvasAssignmentSubmissionFact implements ReadableCanvasDumpArtifact<CanvasAssignmentSubmissionFact.Types> {

  public enum Types {
    submission_fact
  }

  
  /**
   * 
   * Columns

      Name  Type  Description
      submission_id bigint  Foreign key to submission dimension
      assignment_id bigint  Foreign key to assignment dimension
      course_id bigint  Foreign key to course dimension of course associated with the assignment.
      enrollment_term_id  bigint  Foreign Key to enrollment term table
      user_id bigint  Foreign key to user dimension of user who submitted the assignment.
      grader_id bigint  Foreign key to the user dimension of user who graded the assignment.
      course_account_id bigint  (Deprecated) Foreign key to the account dimension of the account associated with the course associated with the assignment. Please use 'account_id' instead.
      enrollment_rollup_id  bigint  Foreign key to the enrollment roll-up dimension table.
      score double precision  Numeric grade given to the submission.
      published_score double precision  Valid only for a graded submission. It reflects the numerical value of the actual score. Referring to our previous example for 'submission_dim.published_grade', let's take two submissions, one for an assignment with a scoring method of 'points' and the other for an assignment with a scoring method of 'letter grade'. If the published grade is '4' out of '5' and 'B' for them, respectively, then they should both have a score of '4' out of '5'. And their 'published_score' values will be identical, '4.0'. Defaults to 'NULL'.
      what_if_score double precision  Valid only if the student ever entered a 'What If' score for an assignment in the Canvas UI. Only the most recent score entered by the student is stored here. Any time a new score is entered, the existing one is overwritten. Defaults to 'NULL'.
      submission_comments_count int Reflects the total number of comments on the submission by anyone/everyone, excluding comments that are flagged as 'hidden'.
      account_id  bigint  Foreign key to the account the submission belongs to.
      assignment_group_id bigint  Foreign key to the assignment group dimension table.
      group_id  bigint  Foreign key to the group_dim table.
      quiz_id bigint  Foreign key to the quiz the quiz submission associated with this submission represents.
      quiz_submission_id  bigint  Foreign key to the quiz_submission_dim table.
      wiki_id bigint  Foreign key to the wiki_dim table.
   * 
   */
  
  @JsonProperty("submission_id")
  private Long submissionId;
  
  @JsonProperty("assignment_id")
  private Long assignmentId;
  
  @JsonProperty("course_id")
  private Long courseId;
  
  @JsonProperty("enrollment_term_id")
  private Long enrollmentTermId;

  @JsonProperty("user_id")
  private Long userId;
  
  @JsonProperty("grader_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> graderId;
  
  @JsonProperty("course_account_id")
  private Long courseAccountId;
  
  @JsonProperty("enrollment_rollup_id")
  private Long enrollmentRollupId;

  @JsonProperty("score")
  @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
  private Optional<Double> score;
  
  @JsonProperty("published_score")
  @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
  private Optional<Double> publishedScore;
  
  @JsonProperty("what_if_score")
  @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
  private Optional<Double> whatIfScore;

  @JsonProperty("submission_comments_count")
  @JsonDeserialize(using = NullableIntegerFieldDeserializer.class)
  private Optional<Integer> submissionCommentsCount;
  
  @JsonProperty("account_id")
  private Long accountId;
  
  @JsonProperty("assignment_group_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> assignmentGroupId;

  @JsonProperty("group_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> groupId;
  
  @JsonProperty("quiz_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> quizId;
  
  @JsonProperty("quiz_submission_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> quizSubmissionId;
  
  @JsonProperty("wiki_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> wikiId;
  


}
