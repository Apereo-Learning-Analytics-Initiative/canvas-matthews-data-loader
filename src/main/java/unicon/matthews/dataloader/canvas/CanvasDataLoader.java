package unicon.matthews.dataloader.canvas;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import unicon.matthews.dataloader.DataLoader;
import unicon.matthews.dataloader.MatthewsClient;
import unicon.matthews.dataloader.canvas.io.deserialize.CanvasDataDumpReader;
import unicon.matthews.dataloader.canvas.io.deserialize.ClassReader;
import unicon.matthews.dataloader.canvas.io.deserialize.EnrollmentReader;
import unicon.matthews.dataloader.canvas.io.deserialize.LineItemReader;
import unicon.matthews.dataloader.canvas.io.deserialize.UserReader;
import unicon.matthews.dataloader.canvas.model.CanvasDataDump;
import unicon.matthews.dataloader.canvas.model.CanvasQuizSubmissionDimension;
import unicon.matthews.dataloader.canvas.model.CanvasQuizSubmissionFact;
import unicon.matthews.dataloader.canvas.model.CanvasQuizSubmissionHistoricalDimension;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.User;

import static unicon.matthews.dataloader.canvas.CanvasDataApiClient.Options;

@Component
public class CanvasDataLoader implements DataLoader {
  
  private static Logger logger = LoggerFactory.getLogger(CanvasDataLoader.class);

  @Autowired
  private MatthewsClient matthewsClient;

  @Autowired
  private CanvasDataApiClient canvasDataApiClient;

  @Override
  public void run() {
    
    try {

      // Example of getting dumps in an inclusive date range, with downloads
//      List<CanvasDataDump> dumps = canvasDataApiClient.getDumps(LocalDate.parse("2017-01-15"),
//              LocalDate.parse("2017-01-19"), Options.builder().withArtifactDownloads().build());

      // Example of getting latest dump, but no download
//      CanvasDataDump dump = canvasDataApiClient.getLatestDump(Options.NONE);

      // Example of getting latest dump, with download
      CanvasDataDump dump = canvasDataApiClient.getLatestDump(Options.builder().withArtifactDownloads().build());

      // Dump passed to the processors below needs to have been downloaded, or they will fail.

      // Example of filtering results to only include a specific artifact (when multiple available) and also to filter
      // those which have end dates and are after a specified date.
      Collection<CanvasQuizSubmissionFact> quizSubmissionFacts = CanvasDataDumpReader.forType(
              CanvasQuizSubmissionFact.class)
              .includeOnly(CanvasQuizSubmissionFact.Types.quiz_submission_fact)
              .withFilter(canvasQuizSubmissionFact ->
                      canvasQuizSubmissionFact.getDate().isPresent() ? canvasQuizSubmissionFact.getDate().get().isAfter(
                              LocalDate.parse("2016-10-21").atStartOfDay(ZoneOffset.UTC).toInstant()) : false
              ).read(dump);
      Collection<CanvasQuizSubmissionDimension> quizSubmissionDimensions = CanvasDataDumpReader.forType(
              CanvasQuizSubmissionDimension.class).read(dump);
      Collection<CanvasQuizSubmissionHistoricalDimension> quizSubmissionHistoricalDimensions =
              CanvasDataDumpReader.forType(CanvasQuizSubmissionHistoricalDimension.class).read(dump);

      Map<String, unicon.matthews.oneroster.Class> classMap = new HashMap<>();
      Map<String, User> userMap = new HashMap<>();
      
      ClassReader courseSectionReader = new ClassReader(dump.getDownloadPath().toString());
      Collection<unicon.matthews.oneroster.Class> classes = courseSectionReader.read();
      if (classes != null) {
        for (unicon.matthews.oneroster.Class klass : classes) {
          matthewsClient.postClass(klass);
          classMap.put(klass.getSourcedId(), klass);
        }
      }
      
      UserReader userReader = new UserReader(dump.getDownloadPath().toString());
      Collection<User> users = userReader.read();
      if (users != null) {
        for (User user : users) {
          matthewsClient.postUser(user);
          userMap.put(user.getSourcedId(), user);
        }
      }
      
      EnrollmentReader enrollmentReader = new EnrollmentReader(dump.getDownloadPath().toString(),classMap,userMap);
      Collection<Enrollment> enrollments = enrollmentReader.read();
      if (enrollments != null) {
        for (Enrollment enrollment : enrollments) {
          matthewsClient.postEnrollment(enrollment);
        }
      }
      
      LineItemReader lineItemReader = new LineItemReader(dump.getDownloadPath().toString(), classMap);
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
