package unicon.matthews.dataloader.canvas.io.deserialize;

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
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unicon.matthews.oneroster.User;

public class UserReader {
  
  private static Logger logger = LoggerFactory.getLogger(UserReader.class);
  
  private static final String USER_DIRECTORY = "/user_dim";
  
  private String downloadDirectory;
  
  public UserReader(String downloadDirectory) {
    if (StringUtils.isBlank(downloadDirectory)) {
      throw new IllegalArgumentException("Download directory cannot be null");
    }
    
    if (downloadDirectory.endsWith("/")) {
      downloadDirectory = StringUtils.stripEnd(downloadDirectory, "/");
    }
    
    this.downloadDirectory = downloadDirectory;
  }

  public Collection<User> read() throws IOException {
    Collection<User> users = null;
    InputStream gzipStream = null;
    BufferedReader buffered = null;
    
    try {
      String path = this.downloadDirectory + USER_DIRECTORY;
      File folder = new File(path);
      Collection<File> files = FileUtils.listFiles(folder, null, false);
      logger.debug("{}",files);
      
      if (files != null && !files.isEmpty()) {
        users = new ArrayList<>();
        
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
            
            String lastName = null;
            String firstName = null;
            String sortableName = sa[15];
            if (StringUtils.isNotBlank(sortableName)) {
              lastName = StringUtils.substringBefore(sortableName, ",");
              firstName = StringUtils.substringAfter(sortableName, ", ");
            }
            
            User user 
              = new User.Builder()
                .withSourcedId(sa[0])
                .withFamilyName(lastName)
                .withGivenName(firstName)
                .build();
            
            users.add(user);
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
    
    
    return users;
  }

}
