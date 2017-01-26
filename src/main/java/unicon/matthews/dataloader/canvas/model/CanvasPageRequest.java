package unicon.matthews.dataloader.canvas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import java.time.Instant;
import java.util.Optional;

/**
 * Canvas data dump pageview requests.
 *
 * <p>The <code>@JsonPropertyOrder</code> designates the tab delimited field order for this artifact.</p>
 *
 * <p>For each attribute description, the sources are from the Request schema link below, unless otherwise cited.</p>
 *
 * <p>The Canvas documentation contains this disclaimer:
 * <blockquote>Pageview requests. Disclaimer: The data in the requests table is a 'best effort' attempt, and is not
 * guaranteed to be complete or wholly accurate. This data is meant to be used for rollups and analysis in the
 * aggregate, _not_ in isolation for auditing, or other high-stakes analysis involving examining single users or small
 * samples. As this data is generated from the Canvas logs files, not a transactional database, there are many places
 * along the way data can be lost and/or duplicated (though uncommon). Additionally, given the size of this data, our
 * processes are often done on monthly cycles for many parts of the requests tables, so as errors occur they can only be
 * rectified monthly.</blockquote></p>
  *
 * @see <a href="https://portal.inshosteddata.com/docs#requests">Canvas Requests Schema</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "id", "timestamp", "timestamp_year", "timestamp_month", "timestamp_day", "user_id", "course_id",
        "root_account_id", "course_account_id", "quiz_id", "discussion_id", "conversation_id", "assignment_id", "url",
        "user_agent", "http_method", "remote_ip", "interaction_micros", "web_application_controller",
        "web_applicaiton_action", "web_application_context_type", "web_application_context_id", "real_user_id",
        "session_id", "user_agent_id", "http_status", "http_version"})
public class CanvasPageRequest implements ReadableCanvasDumpArtifact<CanvasPageRequest.Types> {

    public enum Types {
        requests
    }

    /**
     * <blockquote>Request ID assigned by the canvas system to the request.</blockquote>
     */
    @JsonProperty("id")
    private String id;

    /**
     * <blockquote>Timestamp when the request was made in UTC.</blockquote>
     */
    @JsonProperty("timestamp")
    @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
    private Instant timestamp;

    /**
     * <blockquote>Year when the request was made.</blockquote>
     */
    @JsonProperty("timestamp_year")
    private String timestampYear;

    /**
     * <blockquote>Month when the request was made.</blockquote>
     */
    @JsonProperty("timestamp_month")
    private String timestampMonth;

    /**
     * <blockquote>Day when the request was made.</blockquote>
     */
    @JsonProperty("timestamp_day")
    private String timestampDay;

    /**
     * <blockquote>Foreign key in user_dim for the user that made the request. If the request was made by one user
     * masquerading as another, then this column contains the ID of the user being masqueraded as.</blockquote>
     *
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("user_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> userId;

    /**
     * <blockquote>Foreign key in course_dim for the course that owned the page requested. Set to NULL if not
     * applicable.</blockquote>
     */
    @JsonProperty("course_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> courseId;

    /**
     * <blockquote>Foreign key in account_dim for the root account on which this request was made.</blockquote>
     */
    @JsonProperty("root_account_id")
    private Long rootAccountId;

    /**
     * <blockquote>Foreign key in account_dim for the account the associated course is owned by.</blockquote>
     *
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("course_account_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> courseAccountId;

    /**
     * <blockquote>Foreign key in quiz_dim if the page request is for a quiz, otherwise NULL.</blockquote>
     *
     * <p>This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas
     * documentation does not state that.</p>
     */
    @JsonProperty("quiz_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> quizId;

    /**
     * <blockquote>Foreign key in discussion_dim if page request is for a discussion, otherwise NULL.</blockquote>
     */
    @JsonProperty("discussion_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> discussionId;

    /**
     * <blockquote>Foreign key in conversation_dim if page request is for a conversation, otherwise NULL.</blockquote>
     */
    @JsonProperty("conversation_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> conversationId;

    /**
     * <blockquote>Assignment foreign key if page request is for an assignment, otherwise NULL.</blockquote>
     */
    @JsonProperty("assignment_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> assignmentId;

    /**
     * <blockquote>URL which was requested.</blockquote>
     */
    @JsonProperty("url")
    private String url;

    /**
     * <blockquote>User agent header received from the users browser/client software.</blockquote>
     */
    @JsonProperty("user_agent")
    private String userAgent;

    /**
     * <blockquote>HTTP method/verb (GET, PUT, POST etc.) that was sent with the request.</blockquote>
     */
    @JsonProperty("http_method")
    private String httpMthod;

    /**
     * <blockquote>IP address that was recorded from the request.</blockquote>
     */
    @JsonProperty("remote_ip")
    private String remoteIp;

    /**
     * <blockquote>Total time required to service the request in microseconds.</blockquote>
     */
    @JsonProperty("interaction_micros")
    private Long interactionMicros;

    /**
     * <blockquote></blockquote>
     */
    @JsonProperty("web_application_controller")
    private String webApplicationController;

    /**
     * <blockquote>Controller the Canvas web application used to service this request. (There is a typo in the field
     * name, in order to minimize impact, this will be changed in a future version of Canvas Data.)</blockquote>
     *
     * <p>NOTE: We are not propagating the typo in our field name.</p>
     */
    @JsonProperty("web_applicaiton_action")
    private String webApplicationAction;

    /**
     * <blockquote>Containing object type the Canvas web application used to service this request.</blockquote>
     */
    @JsonProperty("web_application_context_type")
    private String webApplicationContexType;

    /**
     * <blockquote>Containing object's ID the Canvas web application used to service this request.</blockquote>
     */
    @JsonProperty("web_application_context_id")
    private String webApplicationContextId;

    /**
     * <blockquote>If the request was processed by one user masquerading as another, then this column contains the real
     * user ID of the user.</blockquote>
     */
    @JsonProperty("real_user_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> realUserId;

    /**
     * <blockquote>ID of the user's session where this request was made.</blockquote>
     */
    @JsonProperty("session_id")
    private String sessionId;

    /**
     * <blockquote>(Not implemented) Foreign key to the user agent dimension table.</blockquote>
     */
    @JsonProperty("user_agent_id")
    @JsonDeserialize(using = NullableLongFieldDeserializer.class)
    private Optional<Long> userAgentId;

    /**
     * <blockquote>HTTP status of the request.</blockquote>
     */
    @JsonProperty("http_status")
    private String httpStatus;

    /**
     * <blockquote>HTTP protocol version.</blockquote>
     */
    @JsonProperty("http_version")
    private String httpVersion;
}
