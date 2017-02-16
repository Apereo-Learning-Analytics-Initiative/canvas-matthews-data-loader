package unicon.matthews.dataloader.canvas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicon.matthews.dataloader.canvas.io.deserialize.NullableLongFieldDeserializer;
import unicon.matthews.dataloader.canvas.io.deserialize.ReadableCanvasDumpArtifact;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@JsonPropertyOrder({ "discussion_entry_id", "parent_discussion_entry_id", "user_id", "topic_id", "course_id",
        "enrollment_term_id", "course_account_id", "topic_user_id", "topic_assignment_id", "topic_editor_id",
        "enrollment_rollup_id", "message_length" })
public class CanvasDiscussionForumEntryFact implements ReadableCanvasDumpArtifact<CanvasDiscussionForumEntryFact.Types> {

  public enum Types {
      discussion_entry_fact
  }

  /**
   * https://portal.inshosteddata.com/docs#discussion_entry_fact
   *
   Measures for discussion entries. Discussion entries are replies in a discussion topic.

   Type: fact
   Columns
   Name	Type	Description
   discussion_entry_id	bigint	Foreign key to this entries attributes.
   parent_discussion_entry_id	bigint	Foreign key to the reply that it is nested underneath.
   user_id	bigint	Foreign key to the user that created this entry.
   topic_id	bigint	Foreign key to associated discussion topic.
   course_id	bigint	Foreign key to associated course.
   enrollment_term_id	bigint	Foreign Key to enrollment term table
   course_account_id	bigint	Foreign key to account for associated course.
   topic_user_id	bigint	Foreign key to user that posted the associated discussion topic.
   topic_assignment_id	bigint	Foreign key to assignment associated with the entry's discussion topic.
   topic_editor_id	bigint	Foreign key to editor associated with the entry's discussion topic.
   enrollment_rollup_id	bigint	Foreign key to the enrollment roll-up dimension table
   message_length	int	Length of the message in bytes
   */
  
  @JsonProperty("discussion_entry_id")
  private Long discussionEntryId;

  /**
   * This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas documentation does
   * not state that.
   */
  @JsonProperty("parent_discussion_entry_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> parentDiscussionEntryId;
  
  @JsonProperty("user_id")
  private Long userId;
  
  @JsonProperty("topic_id")
  private Long topicId;
  
  @JsonProperty("course_id")
  private Long courseId;
  
  @JsonProperty("enrollment_term_id")
  private Long enrollmentTermId;
  
  @JsonProperty("course_account_id")
  private Long courseAccountId;

  /**
   * This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas documentation does
   * not state that.
   */
  @JsonProperty("topic_user_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> topicUserId;

  /**
   * This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas documentation does
   * not state that.
   */
  @JsonProperty("topic_assignment_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> topicAssignmentId;

  /**
   * This field is actually optional and may be null (data value <em>\N</em>), even though the Canvas documentation does
   * not state that.
   */
  @JsonProperty("topic_editor_id")
  @JsonDeserialize(using = NullableLongFieldDeserializer.class)
  private Optional<Long> topicEditorId;

  @JsonProperty("enrollment_rollup_id")
  private Long enrollmentRollupId;

  @JsonProperty("message_length")
  private int messageLength;
}
