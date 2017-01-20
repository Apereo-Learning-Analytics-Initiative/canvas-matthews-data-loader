package unicon.matthews.dataloader.canvas;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import unicon.matthews.dataloader.DataLoader;
import unicon.matthews.dataloader.MatthewsClient;
import unicon.matthews.dataloader.canvas.exception.CanvasDataConfigurationException;
import unicon.matthews.dataloader.canvas.exception.UnexpectedApiResponseException;
import unicon.matthews.dataloader.canvas.model.CanvasDataDump;
import unicon.matthews.dataloader.canvas.reader.ClassReader;
import unicon.matthews.dataloader.canvas.reader.EnrollmentReader;
import unicon.matthews.dataloader.canvas.reader.LineItemReader;
import unicon.matthews.dataloader.canvas.reader.UserReader;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.User;

@Component
public class CanvasDataLoader implements DataLoader {
  
  private static Logger logger = LoggerFactory.getLogger(CanvasDataLoader.class);
  
  @Value("${downloaddirectory:CANVAS_DUMP}")
  private String downloadDirectory;
  
  @Autowired private MatthewsClient matthewsClient;
  @Autowired private ApiClient apiClient;

  @Override
  public void run() {
    
    try {
      //CanvasDataDump canvasDataDump = apiClient.getLatestDump();
      //canvasDataDump.downloadAllFiles(new File(downloadDirectory));
      
      Map<String, unicon.matthews.oneroster.Class> classMap = new HashMap<>();
      Map<String, User> userMap = new HashMap<>();
      
      ClassReader courseSectionReader = new ClassReader(downloadDirectory);
      Collection<unicon.matthews.oneroster.Class> classes = courseSectionReader.read();
      if (classes != null) {
        for (unicon.matthews.oneroster.Class klass : classes) {
          matthewsClient.postClass(klass);
          classMap.put(klass.getSourcedId(), klass);
        }
      }
      
      UserReader userReader = new UserReader(downloadDirectory);
      Collection<User> users = userReader.read();
      if (users != null) {
        for (User user : users) {
          matthewsClient.postUser(user);
          userMap.put(user.getSourcedId(), user);
        }
      }
      
      EnrollmentReader enrollmentReader = new EnrollmentReader(downloadDirectory,classMap,userMap);
      Collection<Enrollment> enrollments = enrollmentReader.read();
      if (enrollments != null) {
        for (Enrollment enrollment : enrollments) {
          matthewsClient.postEnrollment(enrollment);
        }
      }
      
      LineItemReader lineItemReader = new LineItemReader(downloadDirectory, classMap);
      Collection<LineItem> lineItems = lineItemReader.read();
      if (lineItems != null) {
        for (LineItem li : lineItems) {
          
          matthewsClient.postLineItem(li);
        }
      }
      
    } 
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 

  }

}
