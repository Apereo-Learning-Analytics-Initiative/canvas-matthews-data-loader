package unicon.matthews.dataloader.canvas.io.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasAssignmentDimension;
import unicon.matthews.dataloader.canvas.model.CanvasAssignmentSubmissionFact;
import unicon.matthews.dataloader.canvas.model.CanvasCourseSectionDimension;
import unicon.matthews.dataloader.canvas.model.CanvasDiscussionForumEntryFact;
import unicon.matthews.dataloader.canvas.model.CanvasEnrollmentDimension;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.dataloader.canvas.model.CanvasQuizDimension;
import unicon.matthews.dataloader.canvas.model.CanvasQuizSubmissionFact;
import unicon.matthews.dataloader.canvas.model.CanvasUserDimension;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.User;

@Component
public class CanvasConversionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<Converter<CanvasPageRequest, Optional<Event>>> pageRequestToEventConverters;
    
    @Autowired
    private CanvasClassConverter canvasClassConverter;
    
    @Autowired
    private CanvasUserConverter canvasUserConverter;

    @Autowired
    private CanvasEnrollmentConverter canvasEnrollmentConverter;
    
    @Autowired
    private CanvasAssignmentConverter canvasAssignmentConverter;
    
    @Autowired
    private CanvasQuizConverter canvasQuizConverter;

    @Autowired
    private CanvasQuizSubmissionEventConverter canvasQuizSubmissionEventConverter;

    @Autowired
    private CanvasDiscussionForumEntryToCaliperEventConverter canvasDiscussionForumEntryToCaliperEventConverter;
    
    @Autowired
    private CanvasAssignmentSubmissionConverter canvasAssignmentSubmissionConverter;
    
    public List<Event> convertPageRequests(Collection<CanvasPageRequest> sourceItems,
            SupportingEntities supportingEntities) {

        List<Event> events = new ArrayList<>();

        Optional<Event> event = null;

        for (CanvasPageRequest sourceItem : sourceItems) {

            Optional<Converter<CanvasPageRequest, Optional<Event>>> selectedConverter =
                    pageRequestToEventConverters.stream().filter(converter -> converter.supports(sourceItem)).findFirst();

            if (selectedConverter != null && selectedConverter.isPresent()) {

                event = selectedConverter.get().convert(sourceItem, supportingEntities);

                if (event != null && event.isPresent()) {
                    events.add(event.get());
                    logger.debug("Page Request Conversion PROCESSED by converter {} : From {} > EVENT: {}",
                            selectedConverter.get().getClass().getSimpleName(), sourceItem.toString(),
                            event.get().toString());
                } else {
                    logger.debug("Page Request Conversion PROCESSED by converter {} : From {} > NO EVENT",
                            selectedConverter.get().getClass().getSimpleName(), sourceItem.toString());
                }
            } else {
                logger.debug("Page Request Conversion SKIP request with no matching converter: {}", sourceItem.toString());
            }
        }

        return events;
    }

    public List<unicon.matthews.oneroster.Class> convertCanvasCourseSections(Collection<CanvasCourseSectionDimension> sourceItems,
            SupportingEntities supportingEntities) {
      List<unicon.matthews.oneroster.Class> classes = new ArrayList<>();
      
      Optional<unicon.matthews.oneroster.Class> klass = null;
      
      for (CanvasCourseSectionDimension sourceItem : sourceItems) {
        klass = canvasClassConverter.convert(sourceItem, supportingEntities);
        
        if (klass.isPresent()) {
          classes.add(klass.get());
          logger.debug("Course section PROCESSED from {} to {}", sourceItem.toString(), klass.get().toString());
        }
      }
      
      return classes;
    }
    
    public List<User> convertCanvasUsers(Collection<CanvasUserDimension> sourceItems, 
        SupportingEntities supportingEntities) {
      List<User> users = new ArrayList<>();
      
      Optional<User> user = null;
      
      for (CanvasUserDimension sourceItem : sourceItems) {
        user = canvasUserConverter.convert(sourceItem, supportingEntities);
        
        if (user.isPresent()) {
          users.add(user.get());
          logger.debug("User PROCESSED from {} to {}", sourceItem.toString(), user.get().toString());
        }
      }
      
      return users;
    }
    
    public List<Enrollment> convertCanvasEnrollments(Collection<CanvasEnrollmentDimension> sourceItems, 
        SupportingEntities supportingEntities) {
      List<Enrollment> enrollments = new ArrayList<>();
      
      Optional<Enrollment> enrollment = null;
      
      for (CanvasEnrollmentDimension sourceItem : sourceItems) {
        enrollment = canvasEnrollmentConverter.convert(sourceItem, supportingEntities);
        
        if (enrollment.isPresent()) {
          enrollments.add(enrollment.get());
          logger.debug("Enrollment PROCESSED from {} to {}", sourceItem.toString(), enrollment.get().toString());

        }
      }
      
      return enrollments;
    }
    
    public List<LineItem> convertCanvasAssignments(Collection<CanvasAssignmentDimension> sourceItems,
        SupportingEntities supportingEntities) {
      List<LineItem> lineItems = new ArrayList<>();
      
      Optional<LineItem> lineItem = null;
      
      for (CanvasAssignmentDimension sourceItem : sourceItems) {
        lineItem = canvasAssignmentConverter.convert(sourceItem, supportingEntities);
        
        if (lineItem.isPresent()) {
          lineItems.add(lineItem.get());
          logger.debug("Line item PROCESSED from {} to {}", sourceItem.toString(), lineItem.get().toString());

        }
      }
      
      return lineItems;
    }

    public List<LineItem> convertCanvasQuizes(Collection<CanvasQuizDimension> sourceItems,
        SupportingEntities supportingEntities) {
      List<LineItem> lineItems = new ArrayList<>();
      
      Optional<LineItem> lineItem = null;
      
      for (CanvasQuizDimension sourceItem : sourceItems) {
        lineItem = canvasQuizConverter.convert(sourceItem, supportingEntities);
        
        if (lineItem.isPresent()) {
          lineItems.add(lineItem.get());
          logger.debug("Line item PROCESSED from {} to {}", sourceItem.toString(), lineItem.get().toString());

        }
      }
      
      return lineItems;
    }

    public List<Event> convertCanvasDiscussionForumEntries(Collection<CanvasDiscussionForumEntryFact> forumEntryFacts,
            SupportingEntities supportingEntities) {
        List<Event> events = new ArrayList<>();

        Optional<Event> event = null;

        for (CanvasDiscussionForumEntryFact sourceItem : forumEntryFacts) {

            event = canvasDiscussionForumEntryToCaliperEventConverter.convert(sourceItem, supportingEntities);

            if (event.isPresent()) {
                events.add(event.get());
                logger.debug("Canvas Discussion Forum Entry Fact Conversion PROCESSED: From {} > EVENT: {}",
                        sourceItem.toString(), event.get().toString());
            } else {
                logger.debug("Canvas Discussion Forum Entry Fact Conversion PROCESSED: From {} > NO EVENT",
                        sourceItem.toString());
            }
        }

        return events;
    }

    public List<Event> convertCanvasQuizSubmissions(Collection<CanvasQuizSubmissionFact> quizSubmissionFacts,
            SupportingEntities supportingEntities) {
        List<Event> events = new ArrayList<>();

        Optional<Event> event = null;

        for (CanvasQuizSubmissionFact sourceItem : quizSubmissionFacts) {

            event = canvasQuizSubmissionEventConverter.convert(sourceItem, supportingEntities);

            if (event.isPresent()) {
                events.add(event.get());
                logger.debug("Canvas Quiz Submission Fact Conversion PROCESSED: From {} > EVENT: {}",
                        sourceItem.toString(), event.get().toString());
            } else {
                logger.debug("Canvas Quiz Submission Fact Conversion PROCESSED: From {} > NO EVENT",
                        sourceItem.toString());
            }
        }

        return events;
    }
    
    public List<Event> convertCanvasAssignmentSubmissions(Collection<CanvasAssignmentSubmissionFact> assignmentSubmissionFacts,
        SupportingEntities supportingEntities) {
    List<Event> events = new ArrayList<>();

    Optional<Event> event = null;

    for (CanvasAssignmentSubmissionFact sourceItem : assignmentSubmissionFacts) {

        event = canvasAssignmentSubmissionConverter.convert(sourceItem, supportingEntities);

        if (event.isPresent()) {
            events.add(event.get());
            logger.debug("Canvas Assignment Submission Fact Conversion PROCESSED: From {} > EVENT: {}",
                    sourceItem.toString(), event.get().toString());
        } else {
            logger.debug("Canvas Assignment Submission Fact Conversion PROCESSED: From {} > NO EVENT",
                    sourceItem.toString());
        }
    }

    return events;
}


}
