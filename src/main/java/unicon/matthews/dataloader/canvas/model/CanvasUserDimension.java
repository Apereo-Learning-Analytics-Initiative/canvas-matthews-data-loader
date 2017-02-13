package unicon.matthews.dataloader.canvas.model;

import java.time.Instant;
import java.util.Optional;

import unicon.matthews.dataloader.canvas.io.deserialize.IsoDateTimeWithOptionalFractionOfSecondDeserializer;
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
@JsonPropertyOrder({ "id", "canvas_id", "root_account_id", "name", "time_zone", "created_at",
        "visibility", "school_name", "school_position", "gender",
        "locale", "public", "workflow_state", "birthdate", "country_code", "workflow_state", "sortable_name"})
public class CanvasUserDimension implements ReadableCanvasDumpArtifact<CanvasUserDimension.Types> {
  
  public enum Types {
    user_dim
  }

  /**
   * https://portal.inshosteddata.com/docs#user_dim
   * 
    Columns
    
    Name  Type  Description
    id  bigint  Unique surrogate id for the user.
    canvas_id bigint  Primary key for this user in the Canvas users table.
    root_account_id bigint  Root account associated with this user.
    name  varchar Name of the user
    time_zone varchar User's primary timezone
    created_at  timestamp Timestamp when the user was created in the Canvas system
    visibility  varchar (Deprecated) No longer used in Canvas.
    school_name varchar Used in Trial Versions of Canvas, the school the user is associated with
    school_position varchar Used in Trial Versions of Canvas, the position the user has at the school. E.g. Admin
    gender  varchar The user's gender. This is an optional field and may not be entered by the user.
    locale  varchar The user's locale. This is an optional field and may not be entered by the user.
    public  varchar Used in Trial Versions of Canvas, the type of school the user is associated with
    birthdate timestamp The user's birth date. This is an optional field and may not be entered by the user.
    country_code  varchar The user's country code. This is an optional field and may not be entered by the user.
    workflow_state  varchar Workflow status indicating the status of the user, valid values are: creation_pending, deleted, pre_registered, registered
    sortable_name varchar Name of the user that is should be used for sorting groups of users, such as in the gradebook.             * 
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
  
  @JsonProperty("time_zone")
  private String timezone;

  @JsonProperty("created_at")
  @JsonDeserialize(using = IsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Instant createdAt;

  @JsonProperty("visibility")
  private String visibility;
  
  @JsonProperty("school_name")
  private String schoolName;
  
  @JsonProperty("school_position")
  private String schoolPosition;

  @JsonProperty("gender")
  private String gender;

  @JsonProperty("locale")
  private String locale;
  
  @JsonProperty("public")
  private String publik;

  @JsonProperty("birthdate")
  @JsonDeserialize(using = NullableIsoDateTimeWithOptionalFractionOfSecondDeserializer.class)
  private Optional<Instant> birthdate;

  @JsonProperty("country_code")
  private String countryCode;
  
  @JsonProperty("workflow_state")
  private String workflowState;
  
  @JsonProperty("sortable_name")
  private String sortableName;

}
