package unicon.matthews.dataloader.canvas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Canvas data dump dimensions about quiz submissions.
 *
 * <p>The <code>@JsonPropertyOrder</code> designates the tab delimited field order for this artifact.</p>
 * <p>For each attribute description, the sources are from the Dimension Schema links below, unless otherwise cited.</p>
  *
 * @see <a href="https://portal.inshosteddata.com/docs#quiz_submission_dim">Canvas Quiz Submission Dimension Schema</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "quiz_id", "submission_id", "user_id", "workflow_state",
        "quiz_state_during_submission", "submission_scoring_policy", "submission_source", "has_seen_results",
        "temporary_user_code", "created_at", "updated_at", "started_at", "finished_at", "due_at"})
public class CanvasQuizSubmissionDimension implements ReadableCanvasDumpArtifact {

    @Override
    public List<String> supports() {
        return Arrays.asList("quiz_submission_dim");
    }

    /**
     * <blockquote>Unique surrogate ID for the quiz submission.</blockquote>
     */
    @JsonProperty("id")
    protected long id;

    /**
     * <blockquote>Primary key for this quiz submission in the 'quiz_submissions' Canvas table.</blockquote>
     */
    @JsonProperty("canvas_id")
    protected long canvasId;

    /**
     * <blockquote>ID of the quiz the quiz submission represents. Foreign key to the quiz dimension table.</blockquote>
     */
    @JsonProperty("quiz_id")
    protected long quizId;

    /**
     * <blockquote>ID to the submission the quiz submission represents. Foreign key to the quiz submission dimension table.</blockquote>
     */
    @JsonProperty("submission_id")
    protected long submissionId;

    /**
     * <blockquote>ID of the user (who is a student) who made the submission. Foreign key to the user dimension table.</blockquote>
     */
    @JsonProperty("user_id")
    protected long userId;

    /**
     * This is the only field which differs in field description with the historical quiz submission dimension data.
     *
     * <blockquote>Denotes the current state of the quiz submission. Possible values are 'untaken', 'complete',
     * 'pending_review', 'preview' and 'settings_only'. Defaults to 'untaken'. An 'untaken' quiz submission is recorded
     * as soon as a student starts the quiz taking process, before even answering the first question. 'pending_review'
     * denotes that a manual submission has been made by the student which has not been completely graded yet. This
     * usually happens when one or more questions in the quiz cannot be autograded (e.g.. 'essay_question' type
     * questions). A 'preview' workflow state is recorded when a Teacher or Admin previews a quiz (even a partial one).
     * 'settings_only' pertains only to quiz moderation events. It stores the settings to create and store moderation
     * events before the student has begun an attempt.</blockquote>
     */
    @JsonProperty("workflow_state")
    protected String workflowState;

    /**
     * <blockquote>There can be two types of quiz states during submission, 1. Quiz submission took place after the quiz
     * was manually unlocked after being locked (but only for a particular student such that (s)he can take the quiz
     * even if it's locked for everyone else). 2. Quiz submission was on-time (that is, when the quiz was never locked).
     * So the two possible values are 'manually_unlocked' and 'never_locked'. Defaults to 'never_locked'.</blockquote>
     */
    @JsonProperty("quiz_state_during_submission")
    protected String quizStateDuringSubmission;

    /**
     * <blockquote>Denotes if the score has been manually overridden by a teacher to reflect the score of a previous
     * attempt (as opposed to a score calculated by the quiz's scoring policy. Possible values are 'manually_overridden'
     * or the general quiz scoring policies, i.e. 'keep_highest', 'keep_latest' and 'keep_average'. Defaults to the
     * scoring policy of the quiz the submission is associated with.</blockquote>
     */
    @JsonProperty("submission_scoring_policy")
    protected String submissionScoringPolicy;

    /**
     * <blockquote>Denotes where the submission was received from. Possible values are 'student' and 'test_preview'.
     * Defaults to 'student'.</blockquote>
     */
    @JsonProperty("submission_source")
    protected String submissionSource;

    /**
     * <blockquote>Denotes whether the student has viewed their results to the quiz.</blockquote>
     */
    @JsonProperty("has_seen_results")
    protected String hasSeenResults;

    /**
     * <blockquote>Construct for previewing a quiz.</blockquote>
     */
    @JsonProperty("temporary_user_code")
    protected String temporaryUserCode;

    /**
     * <blockquote>Time when the quiz submission was created.</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that. Primarily may be null in the historical dimension when the submission_state is
     * 'previous_submission'.</p>
     */
    @JsonProperty("created_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> createdAt;

    /**
     * <blockquote>Time when the quiz submission was last updated.</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that. Primarily may be null in the historical dimension when the submission_state is
     * 'previous_submission'.</p>
     */
    @JsonProperty("updated_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> updatedAt;

    /**
     * <blockquote>Time at which the student started the quiz submission.</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that. Primarily may be null in the historical dimension when the submission_state is
     * 'previous_submission'.</p>
     */
    @JsonProperty("started_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> startedAt;

    /**
     * <blockquote>Time at which the student submitted the quiz submission.</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("finished_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> finishedAt;

    /**
     * <blockquote>Time at which the quiz submission will be overdue, and will be flagged as a late submission.</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("due_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> dueAt;
}
