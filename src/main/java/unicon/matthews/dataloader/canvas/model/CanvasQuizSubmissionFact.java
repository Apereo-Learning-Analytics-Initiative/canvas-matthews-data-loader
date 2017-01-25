package unicon.matthews.dataloader.canvas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableDoubleFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIntegerFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import java.time.Instant;
import java.util.Optional;

/**
 * Canvas data dump facts about quiz submissions. This applies to both the latest and historical quiz submission facts.
 *
 * <p>The only difference between quiz_submission_fact and quiz_submission_historical_fact entries is the label of
 * one field which contains the same value (<em>Foreign key to the quiz submission dimension table</em>). Equivalent
 * field names are:
 * <ul>
 *   <li>quiz_submission_id</li>
 *   <li>quiz_submission_historical_id</li>
 * </ul>
 * </p>
 * <p>The <code>@JsonPropertyOrder</code> designates the tab delimited field order for this artifact.</p>
 * <p>For each attribute description, the sources are from the Fact Schema links below, unless otherwise cited.</p>
  *
 * @see <a href="https://portal.inshosteddata.com/docs#quiz_submission_fact">Canvas Quiz Submission Fact Schema</a>
 * @see <a href="https://portal.inshosteddata.com/docs#quiz_submission_historical_fact">Canvas Quiz Submission Historical Fact Schema</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "score", "kept_score", "date", "course_id", "enrollment_term_id", "course_account_id", "quiz_id",
        "assignment_id", "user_id", "submission_id", "enrollment_rollup_id",
        "quiz_submission_id_OR_quiz_submission_historical_id", "quiz_points_possible", "score_before_regrade",
        "fudge_points", "total_attempts", "extra_attempts", "extra_time", "time_taken"})
public class CanvasQuizSubmissionFact implements ReadableCanvasDumpArtifact<CanvasQuizSubmissionFact.Types> {

    public enum Types {
        quiz_submission_fact,
        quiz_submission_historical_fact
    }

    /**
     * <blockquote>Denotes the score for this submission. Its value would be NULL when they are in the 'preview',
     * 'untaken' OR 'settings_only' workflow states (since it is associated with quiz moderation events). Or its value
     * should not be NULL when workflow state is either 'complete' or 'pending_review'. It defaults to NULL.
     * </blockquote>
     */
    @JsonProperty("score")
    @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
    private Optional<Double> score;

    /**
     * <blockquote>For quizzes that allow multiple attempts, this is the actual score that will be associated with the
     * user for this quiz. This score depends on the scoring policy we have for the submission in the quiz submission
     * dimension table, the workflow state being 'completed' or 'pending_review' and the allowed attempts to be greater
     * than 1. Its value can be NULL when not all these required conditions are met.</blockquote>
     */
    @JsonProperty("kept_score")
    @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
    private Optional<Double> keptScore;

    /**
     * <blockquote>Contains the same value as 'finished_at'. Provided to support backward compatibility with the
     * existing table in production.</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that. The related <em>finished_at</em> field referred to in the description which
     * is part of the <em>quiz_submission_dim</em> also does not specify it may be null.</p>
     */
    @JsonProperty("date")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    private Optional<Instant> date;

    /**
     * <blockquote>Foreign key to the course this submission belongs to.</blockquote>
     */
    @JsonProperty("course_id")
    private long courseId;

    /**
     * <blockquote>Foreign key to the enrollment term of the course this submission belongs to.</blockquote>
     */
    @JsonProperty("enrollment_term_id")
    private long enrollmentTermId;

    /**
     * <blockquote>Foreign key to the account of the course this submission belongs to</blockquote>
     */
    @JsonProperty("course_account_id")
    private long courseAccountId;

    /**
     * <blockquote>ID of the quiz the quiz submission represents. Foreign key to the quiz dimension table.</blockquote>
     */
    @JsonProperty("quiz_id")
    private long quizId;

    /**
     * <blockquote>Foreign key to the assignment the quiz belongs to.</blockquote>
     */
    @JsonProperty("assignment_id")
    private long assignmentId;

    /**
     * <blockquote>ID of the user (who is a student) who made the submission. Foreign key to the user dimension
     * table.</blockquote>
     */
    @JsonProperty("user_id")
    private long userId;

    /**
     * <blockquote>ID to the submission the quiz submission represents. Foreign key to the quiz submission dimension
     * table.</blockquote>
     */
    @JsonProperty("submission_id")
    private long submissionId;

    /**
     * <blockquote>Foreign key to the enrollment roll-up dimension table.</blockquote>
     */
    @JsonProperty("enrollment_rollup_id")
    private long enrollmentRollupId;

    /**
     * This is the only field which differs in field name between the two artifact dumps. Same value, so they are mapped
     * to one field.
     * <blockquote>Foreign key to the quiz submission dimension table.</blockquote>
     */
    @JsonProperty("quiz_submission_id_OR_quiz_submission_historical_id")
    private long quizSubmissionId;

    /**
     * <blockquote>Maximum points that can be scored in this quiz.</blockquote>
     */
    @JsonProperty("quiz_points_possible")
    private double quizPointsPossible;

    /**
     * <blockquote>Original score of the quiz submission prior to any re-grading. It's NULL if the submission has never
     * been regraded. Defaults to NULL.</blockquote>
     */
    @JsonProperty("score_before_regrade")
    @JsonDeserialize(using = NullableDoubleFieldDeserializer.class)
    private Optional<Double> scoreBeforeRegrade;

    /**
     * <blockquote>Number of points the quiz submission's score was fudged (changed) by. Values can be negative or
     * positive. Defaults to 0.</blockquote>
     */
    @JsonProperty("fudge_points")
    private double fudgePoints;

    /**
     * <blockquote>Denotes the total number of attempts made by the student for the quiz. Is valid only if the quiz
     * allows multiple attempts.</blockquote>
     */
    @JsonProperty("total_attempts")
    private int totalAttempts;

    /**
     * <blockquote>Number of times the student was allowed to re-take the quiz over the multiple-attempt
     * limit.</blockquote>
     */
    @JsonProperty("extra_attempts")
    private int extraAttempts;

    /**
     * <blockquote>Amount of extra time allowed for the quiz submission, in minutes</blockquote>
     */
    @JsonProperty("extra_time")
    private int extraTimeInMinutes;

    /**
     * <blockquote>Time taken, in seconds, to finish the quiz.</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("time_taken")
    @JsonDeserialize(using = NullableIntegerFieldDeserializer.class)
    private Optional<Integer> timeTakenInSeconds;
}
