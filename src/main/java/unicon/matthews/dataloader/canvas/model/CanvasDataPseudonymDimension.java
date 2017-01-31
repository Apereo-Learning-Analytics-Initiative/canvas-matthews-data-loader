package unicon.matthews.dataloader.canvas.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

/**
 * Canvas data dump user login pseudonym information.
 *
 * <p>The <code>@JsonPropertyOrder</code> designates the tab delimited field order for this artifact.</p>
 *
 * <p>For each attribute description, the sources are from the Pseudonym Dimension Schema links below, unless otherwise
 * cited.</p>
 *
 * @see <a href="https://portal.inshosteddata.com/docs#pseudonym_dim">Canvas Pseudonym Dimension Schema</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "canvas_id", "user_id", "account_id", "workflow_state", "last_request_at", "last_login_at",
        "current_login_at", "last_login_ip", "current_login_ip", "position", "created_at", "updated_at",
        "password_auto_generated", "deleted_at", "sis_user_id", "unique_name", "integration_id",
        "authentication_provider_id"})
public class CanvasDataPseudonymDimension implements ReadableCanvasDumpArtifact<CanvasDataPseudonymDimension.Types> {

    public enum Types {
        pseudonym_dim
    }

    /**
     * <blockquote>Unique surrogate id for the pseudonym.</blockquote>
     */
    @JsonProperty("id")
    protected Long id;

    /**
     * <blockquote>Primary key for this pseudonym in the the Canvas database</blockquote>
     */
    @JsonProperty("canvas_id")
    protected Long canvasId;

    /**
     * <blockquote>Id for the user associated with this pseudonym</blockquote>
     */
    @JsonProperty("user_id")
    protected Long userId;

    /**
     * <blockquote>Id for the account associated with this pseudonym</blockquote>
     */
    @JsonProperty("account_id")
    protected Long accountId;

    /**
     * <blockquote>Workflow status indicating that pseudonym is [deleted] or [active]</blockquote>
     */
    @JsonProperty("workflow_state")
    protected String workflowState;

    /**
     * <blockquote>Timestamp of when the user last logged in with this pseudonym</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("last_request_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> lastRequestAt;

    /**
     * <blockquote>Timestamp of last time a user logged in with this pseudonym</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("last_login_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> lastLoginAt;

    /**
     * <blockquote>Timestamp of when the user logged in</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("current_login_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> currentLoginAt;

    /**
     * <blockquote>IP address recorded the last time a user logged in with this pseudonym</blockquote>
     */
    @JsonProperty("last_login_ip")
    protected String lastLoginIp;

    /**
     * <blockquote>IP address of user's current/last login</blockquote>
     */
    @JsonProperty("current_login_ip")
    protected String currentLoginIp;

    /**
     * <blockquote>Position of user's login credentials</blockquote>
     */
    @JsonProperty("position")
    protected Integer position;

    /**
     * <blockquote>Timestamp when this pseudonym was created in Canvas</blockquote>
     */
    @JsonProperty("created_at")
    @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Instant createdAt;

    /**
     * <blockquote>Timestamp when this pseudonym was last updated in Canvas</blockquote>
     */
    @JsonProperty("updated_at")
    @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Instant updatedAt;

    /**
     * <blockquote>	True if the password has been auto-generated</blockquote>
     */
    @JsonProperty("password_auto_generated")
    protected Boolean passwordAutoGenerated;

    /**
     * <blockquote>Timestamp when the pseudonym was deleted (NULL if the pseudonym is still active)</blockquote>
     */
    @JsonProperty("deleted_at")
    @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    protected Optional<Instant> deletedAt;

    /**
     * <blockquote>Correlated id for the record for this course in the SIS system (assuming SIS integration is
     * configured</blockquote>
     */
    @JsonProperty("sis_user_id")
    protected String sisUserId;

    /**
     * <blockquote>Actual login id for a given pseudonym/account</blockquote>
     */
    @JsonProperty("unique_name")
    protected String uniqueName;

    /**
     * <blockquote>A secondary unique identifier useful for more complex SIS integrations. This identifier must not
     * change for the user, and must be globally unique.</blockquote>
     */
    @JsonProperty("integration_id")
    protected String integrationId;

    /**
     * <blockquote>The authentication provider this login is associated with. This can be the integer ID of the
     * provider, or the type of the provider (in which case, it will find the first matching provider</blockquote>
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("authentication_provider_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    protected Optional<Long> authenticationProviderId;
}
