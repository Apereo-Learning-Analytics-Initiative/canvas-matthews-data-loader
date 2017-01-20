package unicon.matthews.dataloader.canvas.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.LineItemCategory;
import unicon.matthews.oneroster.Status;

public class LineItemReader {
  private static Logger logger = LoggerFactory.getLogger(LineItemReader.class);

  private Map<String, Class> classMap;
  private static final String [] DIRECTORIES = {"/assignment_dim", "/discussion_topic_dim", "/quiz_dim"};
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private String downloadDirectory;

  public LineItemReader(String downloadDirectory, Map<String, Class> classMap) {
    if (StringUtils.isBlank(downloadDirectory)) {
      throw new IllegalArgumentException("Download directory cannot be null");
    }
    
    if (downloadDirectory.endsWith("/")) {
      downloadDirectory = StringUtils.stripEnd(downloadDirectory, "/");
    }
    
    this.downloadDirectory = downloadDirectory;
    this.classMap = classMap;
  }

  public Collection<LineItem> read() throws IOException {
    Collection<LineItem> lineItems = null;
    InputStream gzipStream = null;
    BufferedReader buffered = null;
    
    try {
      
      for (String dir : DIRECTORIES) {
        String path = this.downloadDirectory + dir;
        File folder = new File(path);
        Collection<File> files = FileUtils.listFiles(folder, null, false);
        logger.debug("{}",files);
        
        if (files != null && !files.isEmpty()) {
          lineItems = new ArrayList<>();
          
          for (File file : files) {

            InputStream fileStream = new FileInputStream(file);
            gzipStream = new GZIPInputStream(fileStream);
            Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
            buffered = new BufferedReader(decoder);
            
            String content;
            while ((content = buffered.readLine()) != null) {
              logger.trace(content);
              StringTokenizer st = new StringTokenizer(content,"\t");
              String [] sa = new String [st.countTokens()];
              int i = 0;
              while (st.hasMoreTokens()) {
                sa[i] = st.nextToken();
                i++;
              }
              logger.debug(Arrays.toString(sa));
              
              // TODO - Address line item categories
              
              String sourcedId = null;
              String title = null;
              LocalDateTime dueDate = null;
              LineItemCategory lineItemCategory = null;
              String classId = null;
              
              if (dir.equals("/assignment_dim")) {
                sourcedId = sa[0];
                title = sa[3];
                lineItemCategory =new LineItemCategory.Builder()
                  .withSourcedId(UUID.randomUUID().toString())
                  .withStatus(Status.active)
                  .withTitle("assignment")
                  .build();
                
                String dueDateString = sa[5];
                if (StringUtils.isNotBlank(dueDateString) && StringUtils.containsNone(dueDateString, "\\")) {
                  dueDate = LocalDateTime.parse(dueDateString,formatter);
                }
                
                classId = sa[2];
              }
              else if (dir.equals("/discussion_topic_dim")) {
                sourcedId = sa[0];
                title = sa[2];
                lineItemCategory =new LineItemCategory.Builder()
                  .withSourcedId(UUID.randomUUID().toString())
                  .withStatus(Status.active)
                  .withTitle("discussion")
                  .build();
                
//                String dueDateString = sa[5];
//                dueDate = LocalDateTime.parse(dueDateString,formatter);
                
                //classId = sa[2];
              }
              else {
                sourcedId = sa[0];
                title = sa[3];
                lineItemCategory =new LineItemCategory.Builder()
                  .withSourcedId(UUID.randomUUID().toString())
                  .withStatus(Status.active)
                  .withTitle("quiz")
                  .build();
                
                String dueDateString = sa[29];
                if (StringUtils.isNotBlank(dueDateString) && StringUtils.containsNone(dueDateString, "\\")) {
                  dueDate = LocalDateTime.parse(dueDateString,formatter);
                }
                
                classId = sa[7];
              }
              
              Class klass = null;
              
              if (StringUtils.isNotBlank(classId)) {
                klass = classMap.get(classId);
              }
              
              LineItem lineItem
                = new LineItem.Builder()
                    .withSourcedId(sourcedId)
                    .withTitle(title)
                    .withCategory(lineItemCategory)
                    .withDueDate(dueDate)
                    .withClass(klass)
                    .build();
              
              lineItems.add(lineItem);
              
              /**
               * https://portal.inshosteddata.com/docs#assignment_dim
               * 
               * Columns

                  Name  Type  Description
                  id  bigint  Unique surrogate ID for the assignment.
                  canvas_id bigint  Primary key for this record in the Canvas assignments table.
                  course_id bigint  Foreign key to the course associated with this assignment
                  title varchar Title of the assignment
                  description text  Long description of the assignment
                  due_at  timestamp Timestamp for when the assignment is due
                  unlock_at timestamp Timestamp for when the assignment is unlocked or visible to the user
                  lock_at timestamp Timestamp for when the assignment is locked
                  points_possible double precision  Total points possible for the assignment
                  grading_type  varchar Describes how the assignment will be graded (gpa_scale, pass_fail, percent, points, not_graded, letter_grade)
                  submission_types  varchar Comma separated list of valid methods for submitting the assignment (online_url, media_recording, online_upload, online_quiz, external_tool, online_text_entry, online_file_upload)
                  workflow_state  varchar Current workflow state of the assignment. Possible values are unpublished, published and deleted
                  created_at  timestamp Timestamp of the first time the assignment was entered into the system
                  updated_at  timestamp Timestamp of the last time the assignment was updated
                  peer_review_count int The number of pears to assign for review if using algorithmic assignment
                  peer_reviews_due_at timestamp Timestamp for when peer reviews should be completed
                  peer_reviews_assigned boolean True if all peer reviews have been assigned
                  peer_reviews  boolean True if peer reviews are enabled for this assignment
                  automatic_peer_reviews  boolean True if peer reviews are assigned algorithmically (vs. letting the instructor make manual assignments)
                  all_day boolean True if A specific time for when the assignment is due was not given. The effective due time will be 11:59pm.
                  all_day_date  date  The date version of the due date if the all_day flag is true.
                  could_be_locked boolean True if the assignment is under a module that can be locked
                  grade_group_students_individually boolean True if students who submit work as a group will each receive individual grades (vs one grade that is copied to all group members)
                  anonymous_peer_reviews  boolean (currently unimplemented, do not use)
                  muted boolean Student cannot see grades left on the assignment.
                  assignment_group_id bigint  Foreign key to the assignment group dimension table.
               */
              
              /**
               * https://portal.inshosteddata.com/docs#discussion_topic_dim
               * 
               * Columns

                  Name  Type  Description
                  id  bigint  Unique surrogate id for the discussion topic.
                  canvas_id bigint  Primary key to the discussion_topics table in Canvas
                  title varchar Title of the discussion topic
                  message text  Message text for the discussion topic.
                  type  varchar Discussion topic type. Two types are default (blank) and announcement.
                  workflow_state  varchar Workflow state for this discussion topic. Valid states are unpublished, active, locked, deleted, and post_delayed
                  last_reply_at timestamp Timestamp of the last reply to this topic.
                  created_at  timestamp Timestamp when the discussion topic was first saved in the system.
                  updated_at  timestamp Timestamp when the discussion topic was last updated in the system.
                  delayed_post_at timestamp Timestamp when the discussion topic was/will be delay-posted
                  posted_at timestamp Timestamp when the discussion topic was posted
                  deleted_at  timestamp Timestamp when the discussion topic was deleted.
                  discussion_type varchar Type of discussion topic: default(blank), side_comment, threaded. threaded indicates that replies are threaded where side_comment indicates that replies in the discussion are flat. See related Canvas Guide https://guides.instructure.com/m/4152/l/60423-how-do-i-create-a-threaded-discussion
                  pinned  boolean True if the discussion topic has been pinned
                  locked  boolean True if the discussion topic has been locked
               */
              
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
            }
              
          }
        }

      }
      
      
    }
    catch (Exception e) {
      logger.error(e.getMessage(),e);
    }
    finally {
      if (gzipStream != null) {
        gzipStream.close();
      }
      
      if (buffered != null) {
        buffered.close();
      }
    }
    
    
    return lineItems;
  }

}
