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
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Status;

public class ClassReader {
  
  private static Logger logger = LoggerFactory.getLogger(ClassReader.class);
  
  private static final String COURSE_SECTION_DIRECTORY = "/course_section_dim";
  
  private String downloadDirectory;
  
  public ClassReader(String downloadDirectory) {
    if (StringUtils.isBlank(downloadDirectory)) {
      throw new IllegalArgumentException("Download directory cannot be null");
    }
    
    if (downloadDirectory.endsWith("/")) {
      downloadDirectory = StringUtils.stripEnd(downloadDirectory, "/");
    }
    
    this.downloadDirectory = downloadDirectory;
  }
  
  public Collection<Class> read() throws IOException {
    Collection<Class> classes = null;
    InputStream gzipStream = null;
    BufferedReader buffered = null;
    
    try {
      String path = this.downloadDirectory + COURSE_SECTION_DIRECTORY;
      File folder = new File(path);
      Collection<File> files = FileUtils.listFiles(folder, null, false);
      logger.debug("{}",files);
      
      if (files != null && !files.isEmpty()) {
        classes = new ArrayList<>();
        
        for (File file : files) {

          InputStream fileStream = new FileInputStream(file);
          gzipStream = new GZIPInputStream(fileStream);
          Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
          buffered = new BufferedReader(decoder);
          
          String content;
          while ((content = buffered.readLine()) != null) {
            logger.debug(content);
            StringTokenizer st = new StringTokenizer(content,"\t");
            String [] sa = new String [st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
              sa[i] = st.nextToken();
              i++;
            }
            logger.debug(Arrays.toString(sa));
            
            /**
             * https://portal.inshosteddata.com/docs#course_section_dim
             * 
             * Columns

                Name  Type  Description
                id  bigint  Unique surrogate id for the course section.
                canvas_id bigint  Primary key for this record in the Canvas course_sections table.
                name  varchar Name of the section
                course_id bigint  Foreign key to the associated course
                enrollment_term_id  bigint  Foreign key to the associated enrollment term
                default_section boolean True if this is the default section
                accepting_enrollments boolean True if this section is open for enrollment
                can_manually_enroll boolean Deprecated
                start_at  timestamp Section start date
                end_at  timestamp Section end date
                created_at  timestamp Timestamp for when this section was entered into the system.
                updated_at  timestamp Timestamp for when the last time the section was updated
                workflow_state  varchar Life-cycle state for section. (active, deleted)
                restrict_enrollments_to_section_dates boolean True when "Users can only participate in the course between these dates" is checked
                nonxlist_course_id  bigint  The course id for the original course if this course has been cross listed
                sis_source_id varchar Id for the correlated record for the section in the SIS (assuming SIS integration has been properly configured)
             */
            
            boolean activeStatus = sa[12].equals("active");
            Status status = Status.inactive;
            if (activeStatus) {
              status = Status.active;
            }
            
            Map<String, String> metadata = new HashMap<>();
            metadata.put("http://unicon/vocabulary/v1/sourceSystem", "CANVAS");
            
            String start_at = sa[8];
            String end_at = sa[9];
            
            if (StringUtils.isNotBlank(start_at) && StringUtils.containsNone(start_at, "\\")) {
              metadata.put("http://unicon/vocabulary/v1/classStartDate", StringUtils.substringBefore(start_at, " "));
            }
            
            if (StringUtils.isNotBlank(end_at) && StringUtils.containsNone(end_at, "\\")) {
              metadata.put("http://unicon/vocabulary/v1/classEndDate", StringUtils.substringBefore(end_at, " "));
            }           
            
            Class klass
              = new Class
                .Builder()
                  .withSourcedId(sa[0])
                  .withStatus(status)
                  .withTitle(sa[2])
                  .withMetadata(metadata)
                  .build();
            
            classes.add(klass);
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
    
    
    return classes;
  }

}
