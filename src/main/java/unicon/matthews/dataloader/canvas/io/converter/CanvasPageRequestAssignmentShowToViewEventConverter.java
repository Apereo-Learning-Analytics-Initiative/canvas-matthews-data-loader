package unicon.matthews.dataloader.canvas.io.converter;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasDataPseudonymDimension;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.User;

@Component
public class CanvasPageRequestAssignmentShowToViewEventConverter
  implements Converter<CanvasPageRequest, Optional<Event>> {
  
  private static Logger logger = LoggerFactory.getLogger(CanvasPageRequestAssignmentShowToViewEventConverter.class);

  @Override
  public boolean supports(CanvasPageRequest source) {
    return (source.getWebApplicationController().equalsIgnoreCase("assignments")) &&
        (source.getWebApplicationAction().equalsIgnoreCase("show")) &&
        source.getHttpStatus().equals("200");
  }

  @Override
  public Optional<Event> convert(CanvasPageRequest source, SupportingEntities supportingEntities) {
    
    
    logger.debug("Source: {}",source);
    
    Optional<Event> result = null;

    Optional<Long> userId = source.getUserId();

    if (!userId.isPresent()) {
        result = Optional.empty();
    } else {

        Optional<User> maybeUser = supportingEntities.getUsers().values().stream().filter(u -> u.getSourcedId().equalsIgnoreCase(
            String.valueOf(userId.get()))).findFirst();
        
        if (maybeUser != null & maybeUser.isPresent()) {
          User user = maybeUser.get();
          
          CanvasDataPseudonymDimension pseudonym = supportingEntities.getPseudonymDimensions().stream().filter(
                  p -> p.getUserId().toString().equalsIgnoreCase(String.valueOf(userId.get()))
          ).findFirst().get();


          String userLogin = pseudonym.getUniqueName();
          String rootAccountId = source.getRootAccountId().toString();

          LocalDateTime eventTime = LocalDateTime.ofInstant(source.getTimestamp(), ZoneId.of("UTC"));

          Event event;
          Enrollment enrollment = null;
          if (source.getCourseId().isPresent()) {
              String courseId = source.getCourseId().get().toString();
              enrollment = supportingEntities.getEnrollments().values().stream().filter(
                      e -> e.getKlass().getSourcedId().equalsIgnoreCase(courseId)).findFirst().get();
              
              Entity assignmentObject = null;
              if (source.getAssignmentId() != null 
                    && source.getAssignmentId().isPresent()) {
                assignmentObject
                  = new Entity.Builder()
                    .withId(String.valueOf(source.getAssignmentId().get()))
                    .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.ASSIGNABLE_DIGITAL_RESOURCE)
                    .build();
              }
              else {
                assignmentObject
                = new Entity.Builder()
                  .withId(source.getUrl())
                  .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.ASSIGNABLE_DIGITAL_RESOURCE)
                  .build();
              }
              
              event = EventBuilderUtils.usingViewedEventType()
                      .withObject(assignmentObject)
                      .withEventTime(eventTime)
                      .withAgent(usingPersonType(user, String.valueOf(userId.get()), userLogin, rootAccountId).build())
                      .withGroup(usingCourseSectionGroup(enrollment).build())
                      .withMembership(usingMembership(enrollment).build())
                      .withFederatedSession(source.getSessionId())
                      .build();
              
              result = Optional.of(event);
          }
          else {
            result = Optional.empty();
          }
        }      
        else {
          result = Optional.empty();
        }
    }

    return result;
  }

}
