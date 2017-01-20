package unicon.matthews.dataloader.canvas.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;

public class EnrollmentReader {
  private static Logger logger = LoggerFactory.getLogger(EnrollmentReader.class);
  
  private static final String ENROLLMENT_DIRECTORY = "/enrollment_dim";
  
  private String downloadDirectory;
  private Map<String, Class> classMap;
  private Map<String, User> userMap;
  
  public EnrollmentReader(String downloadDirectory, Map<String, Class> classMap, Map<String,User> userMap) {
    if (StringUtils.isBlank(downloadDirectory)) {
      throw new IllegalArgumentException("Download directory cannot be null");
    }
    
    if (downloadDirectory.endsWith("/")) {
      downloadDirectory = StringUtils.stripEnd(downloadDirectory, "/");
    }
    
    this.downloadDirectory = downloadDirectory;
    this.classMap = classMap;
    this.userMap = userMap;
  }

  public Collection<Enrollment> read() throws IOException {
    Collection<Enrollment> enrollments = null;
    InputStream gzipStream = null;
    BufferedReader buffered = null;
    
    try {
      String path = this.downloadDirectory + ENROLLMENT_DIRECTORY;
      File folder = new File(path);
      Collection<File> files = FileUtils.listFiles(folder, null, false);
      logger.debug("{}",files);
      
      if (files != null && !files.isEmpty()) {
        enrollments = new ArrayList<>();
        
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
            
            /**
             * https://portal.inshosteddata.com/docs#enrollment_dim
             * 
             * 
             * Columns

                Name  Type  Description
                id  bigint  Unique surrogate id for the enrollment.
                canvas_id bigint  Primary key for this record in the Canvas enrollments table
                root_account_id bigint  Root account id associated with this enrollment
                course_section_id bigint  Foreign key to the course section for this enrollment
                role_id bigint  Foreign key to the role of the person enrolled in the course
                type  varchar Enrollment type: TaEnrollment, DesignerEnrollment, StudentEnrollment, TeacherEnrollment, StudentViewEnrollment, ObserverEnrollment
                workflow_state  varchar Workflow state for enrollment: active, completed, rejected, deleted, invited, creation_pending
                created_at  timestamp Timestamp for when this section was entered into the system.
                updated_at  timestamp Timestamp for when the last time the section was updated
                start_at  timestamp Enrollment start date
                end_at  timestamp Enrollment end date
                completed_at  timestamp Enrollment completed date
                self_enrolled boolean Enrollment was created via self-enrollment
                sis_source_id varchar (Deprecated) No longer used in Canvas.
                course_id bigint  Foreign key to course for this enrollment
                user_id bigint  Foreign key to user for the enrollment
             * 
             * 
             */
            
            Role role = null;
            String roleType = sa[5];
            if ("StudentEnrollment".equalsIgnoreCase(roleType) 
                || "StudentViewEnrollment".equalsIgnoreCase(roleType) 
                || "ObserverEnrollment".equalsIgnoreCase(roleType) ) {
              role = Role.student;
            }
            else {
              role = Role.teacher;
            }
            
            Status status = null;
            if ("active".equalsIgnoreCase(sa[6])) {
              status = Status.active;
            }
            else {
              status = Status.inactive;
            }
            
            Enrollment enrollment
              = new Enrollment.Builder()
                .withSourcedId(sa[0])
                .withRole(role)
                .withStatus(status)
                .withKlass(classMap.get(sa[3]))
                .withUser(userMap.get(sa[15]))
                .build();
            
            enrollments.add(enrollment);
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
    
    
    return enrollments;
  }

}
