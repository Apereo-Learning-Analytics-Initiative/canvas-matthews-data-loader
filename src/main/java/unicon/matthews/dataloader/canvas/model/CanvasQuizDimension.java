package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableDoubleFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "root_account_id", "name", "points_possible", "description",
        "quiz_type", "course_id", "assignment_id", "workflow_state",
        "scoring_policy", "anonymous_submissions", "display_questions", "answer_display_order", "go_back_to_previous_question", "could_be_locked",
        "browser_lockdown", "browser_lockdown_for_displaying_results", "browser_lockdown_monitor", "ip_filter", "show_results", "show_correct_answers",
        "show_correct_answers_at", "hide_correct_answers_at", "created_at", "updated_at", "published_at",
        "unlock_at", "lock_at", "due_at", "deleted_at"})
public class CanvasQuizDimension implements ReadableCanvasDumpArtifact<CanvasQuizDimension.Types> {
  public enum Types {
    quiz_dim
  }

  /**
   * https://portal.inshosteddata.com/docs#quiz_dim
   * 
   * Columns

      Name  Type  Description
      id  bigint  Unique surrogate ID for the quiz.
      canvas_id bigint  Primary key for this quiz in the quizzes table.
      root_account_id bigint  Root account ID associated with this quiz.
      name  varchar Name of the quiz. Equivalent Canvas API field -> 'title'.
      points_possible double precision  Total point value given to the quiz.
      description text  Description of the quiz.
      quiz_type varchar Type of quiz. Possible values are 'practice_quiz', 'assignment', 'graded_survey' and 'survey'. Defaults to 'NULL'.
      course_id bigint  Foreign key to the course the quiz belongs to.
      assignment_id bigint  Foreign key to the assignment the quiz belongs to.
      workflow_state  varchar Denotes where the quiz is in the workflow. Possible values are 'unpublished', 'published' and 'deleted'. Defaults to 'unpublished'.
      scoring_policy  varchar Scoring policy for a quiz that students can take multiple times. Is required and only valid if allowed_attempts > 1. Possible values are 'keep_highest', 'keep_latest' and 'keep_average'. Defaults to 'keep_highest'.
      anonymous_submissions varchar Dictates whether students are allowed to submit the quiz anonymously. Possible values are 'allow_anonymous_submissions' and 'disallow_anonymous_submissions'. Defaults to 'disallow_anonymous_submissions'.
      display_questions varchar Policy for displaying the questions in the quiz. Possible values are 'multiple_at_a_time' and 'one_at_a_time'. Defaults to 'multiple_at_a_time'. Equivalent Canvas API field -> 'one_question_at_a_time'.
      answer_display_order  varchar Policy for displaying the answers for each question in the quiz. Possible values are 'in_order' and 'shuffled'. Defaults to 'in_order'. Equivalent Canvas API field -> 'shuffle_answers'.
      go_back_to_previous_question  varchar Policy on going back to the previous question. Is valid only if 'display_questions' is set to 'one_at_a_time'. Possible values are 'allow_going_back' and 'disallow_going_back'. Defaults to 'allow_going_back'. Equivalent Canvas API field -> 'cant_go_back'.
      could_be_locked varchar Dictates if the quiz can be locked or not. Possible values are 'allow_locking' and 'disallow_locking'. Defaults to 'disallow_locking'.
      browser_lockdown  varchar Dictates whether the browser has locked-down when the quiz is being taken. Possible values are 'required' and 'not_required'. Defaults to 'not_required'.
      browser_lockdown_for_displaying_results varchar Dictates whether the browser has to be locked-down to display the results. Is valid only if 'hide_results' is set to 'never' or 'until_after_last_attempt' (for the results to be displayed after the last attempt). Possible values are 'required' and 'not_required'. Defaults to 'not_required'.
      browser_lockdown_monitor  varchar Dictates whether a browser lockdown monitor is required. Possible values are 'required' and 'not_required'. Defaults to 'not_required'.
      ip_filter varchar Restricts access to the quiz to computers in a specified IP range. Filters can be a comma-separated list of addresses, or an address followed by a mask.
      show_results  varchar Dictates whether or not quiz results are shown to students. If set to 'always', students can see their results after any attempt and if set to 'never', students can never see their results. If 'dw_quiz_fact.allowed_attempts > 1' then when set to 'always_after_last_attempt', students can only see their results always, but only after their last attempt. Similarly, if set to 'only_once_after_last_attempt', then students can see their results only after their last attempt, that too only once. Possible values are 'always', 'never', 'always_after_last_attempt' and 'only_once_after_last_attempt'. Defaults to 'always'. Equivalent Canvas API field -> 'hide_results' combined with 'one_time_results'.
      show_correct_answers  varchar Dictates whether correct answers are shown when are results are viewed. It's valid only if 'show_results' is set to 'always'. Possible values are 'always', 'never', 'only_once_after_last_attempt' and 'always_after_last_attempt' (Last two are only valid if 'dw_quiz_fact.allowed_attempts > 1') which have a behavior similar to 'show_results'. Defaults to 'always'. Equivalent Canvas API field -> 'show_correct_answers' combined with 'show_correct_answers_last_attempt'.
      show_correct_answers_at timestamp Day/Time when the correct answers would be shown.
      hide_correct_answers_at timestamp Day/Time when the correct answers are to be hidden.
      created_at  timestamp Time when the quiz was created.
      updated_at  timestamp Time when the quiz was last updated.
      published_at  timestamp Time when the quiz was published.
      unlock_at timestamp Day/Time when the quiz is to be unlocked for students.
      lock_at timestamp Day/Time when the quiz is to be locked for students.
      due_at  timestamp Day/Time when the quiz is due.
      deleted_at  timestamp Time when the quiz was deleted.

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
  
  @JsonProperty("points_possible")
  @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
  private Optional<Double> pointsPossible;
    
  @JsonProperty("description")
  private String description;
  
  @JsonProperty("quiz_type")
  private String quizType;
  
  @JsonProperty("course_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> courseId;
 
  @JsonProperty("assignment_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> assignmentId;
  
  @JsonProperty("workflow_state")
  private String workflowState;
  
  @JsonProperty("scoring_policy")
  private String scoringPolicy;

  @JsonProperty("anonymous_submissions")
  private String anonymousSubmissions;
  
  @JsonProperty("display_questions")
  private String displayQuestions;
  
  @JsonProperty("answer_display_order")
  private String answerDisplayOrder;

  @JsonProperty("go_back_to_previous_question")
  private String goBackToPreviousQuestion;
  
  @JsonProperty("could_be_locked")
  private String couldBeLocked;
  
  @JsonProperty("browser_lockdown")
  private String browserLockdown;
  
  @JsonProperty("browser_lockdown_for_displaying_results")
  private String browserLockdownForDisplayingResults;
  
  @JsonProperty("browser_lockdown_monitor")
  private String browserLockdownMonitor;

  @JsonProperty("ip_filter")
  private String ipFilter;
  
  @JsonProperty("show_results")
  private String showResults;
  
  @JsonProperty("show_correct_answers")
  private String showCorrectAnswers;

  @JsonProperty("show_correct_answers_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> showCorrectAnswersAt;
  
  @JsonProperty("hide_correct_answers_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> hideCorrectAnswersAt;
  
  @JsonProperty("created_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant createdAt;
  
  @JsonProperty("updated_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> updatedAt;
  
  @JsonProperty("published_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> publishedAt;

  @JsonProperty("unlock_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> unlockAt;

  @JsonProperty("lock_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> lockAt;

  @JsonProperty("due_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> dueAt;

  @JsonProperty("delete_at")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> deleteAt;

}
