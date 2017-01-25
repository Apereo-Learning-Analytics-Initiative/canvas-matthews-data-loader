package unicon.matthews.dataloader.canvas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Canvas data dump dimensions about historical quiz submissions.
 *
 * <p>The only difference between quiz_submission_dim and quiz_submission_historical_dim entries is the field
 * description of the <em>workflow_state</em> field, and the additional two fields in this subclass.
 *
 * <p>Note that the primary reason the timestamp fields ("created_at", "updated_at", "started_at", "finished_at", and
 * "due_at") are optional is due to them being null in the Quiz Submission Historical Dimension if the submission_state
 * is 'previous_submission'</p>
 *
 * <p>The <code>@JsonPropertyOrder</code> designates the tab delimited field order for this artifact.</p>
 *
 * <p>For each attribute description, the sources are from the Dimension Schema links below, unless otherwise cited.</p>
  *
 * @see <a href="https://portal.inshosteddata.com/docs#quiz_submission_historical_dim">Canvas Quiz Submission Historical Dimension Schema</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "quiz_id", "submission_id", "user_id", "version_number", "submission_state",
        "workflow_state", "quiz_state_during_submission", "submission_scoring_policy", "submission_source",
        "has_seen_results", "temporary_user_code", "created_at", "updated_at", "started_at", "finished_at", "due_at"})
public class CanvasQuizSubmissionHistoricalDimension extends CanvasQuizSubmissionDimension {

    public enum Types {
        quiz_submission_historical_dim
    }

    /**
     * Must override supports to designate the proper artifact types since the parent class implements
     * <code>ReadableCanvasDumpArtifact</T></code> and by default the types are determined based on the type parameter.
     * @return a list of Canvas data dump artifact names that this type supports
     */
    @Override
    public List<? extends Enum> supports() {
        return Arrays.asList(Types.values());
    }

    /**
     * <blockquote>Version number of this quiz submission.</blockquote>
     */
    @JsonProperty("version_number")
    private int versionNumber;

    /**
     * <blockquote>Denotes if the quiz submission is a current or previous submission. Possible values are
     * 'current_submission' and 'previous_submission'. Defaults to 'current_submission'.</blockquote>
     */
    @JsonProperty("submission_state")
    private String submissionState;

    /**
     * The description of the <em>workflow_state</em> differs in the historical dimension. Its description is below,
     * added to this getter as a means of highlighting the difference and showing it without really affecting the model.
     * <blockquote>Denotes the current state of the quiz submission. Possible values are 'untaken', 'complete',
     * 'pending_review', 'preview' and 'settings_only'. Out of these, 'settings_only' pertains only to quiz moderation
     * events. It stores the settings to create and store moderation events before the student has begun an attempt.
     * Defaults to 'untaken'.</blockquote>
     */
    public String getWorkFlowState() {
        return super.getWorkflowState();
    }
}
