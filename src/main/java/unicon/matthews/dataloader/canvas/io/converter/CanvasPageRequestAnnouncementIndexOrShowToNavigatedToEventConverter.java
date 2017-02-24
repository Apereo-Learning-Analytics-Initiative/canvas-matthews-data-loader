package unicon.matthews.dataloader.canvas.io.converter;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.User;

@Component
public class CanvasPageRequestAnnouncementIndexOrShowToNavigatedToEventConverter 
  implements Converter<CanvasPageRequest, Optional<Event>> {

  private static Logger logger = LoggerFactory.getLogger(CanvasPageRequestAnnouncementIndexOrShowToNavigatedToEventConverter.class);

  @Override
  public boolean supports(CanvasPageRequest source) {
    return (source.getWebApplicationController().equalsIgnoreCase("announcements")) &&
        (source.getWebApplicationAction().equalsIgnoreCase("show") || source.getWebApplicationAction().equalsIgnoreCase("index")) &&
        source.getHttpStatus().equals(String.valueOf(HttpStatus.OK.value()));
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
          
          LocalDateTime eventTime = LocalDateTime.ofInstant(source.getTimestamp(), ZoneId.of("UTC"));

          Event event;
          Enrollment enrollment = null;
          if (source.getCourseId().isPresent()) {
              String courseId = source.getCourseId().get().toString();
              enrollment = supportingEntities.getEnrollments().values().stream().filter(
                      e -> e.getKlass().getSourcedId().equalsIgnoreCase(courseId)).findFirst().get();
              
              Entity resource = null;
              if (source.getDiscussionId() != null 
                    && source.getDiscussionId().isPresent()) {
                resource
                  = new Entity.Builder()
                    .withId(String.valueOf(source.getDiscussionId().get()))
                    .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.DIGITAL_RESOURCE)
                    .build();
              }
              else {
                resource
                = new Entity.Builder()
                  .withId(source.getUrl())
                  .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.DIGITAL_RESOURCE)
                  .build();
              }
              
              event = EventBuilderUtils.usingNavigationEventType()
                      .withObject(resource)
                      .withEventTime(eventTime)
                      .withAgent(usingPersonType(user, user.getUserId(), supportingEntities.getUserEmailMap().get(
                              user.getSourcedId()), source.getRootAccountId().toString()).build())
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
