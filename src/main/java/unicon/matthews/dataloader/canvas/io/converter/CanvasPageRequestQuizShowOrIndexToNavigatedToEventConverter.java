package unicon.matthews.dataloader.canvas.io.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;

@Component
public class CanvasPageRequestQuizShowOrIndexToNavigatedToEventConverter
        implements Converter<CanvasPageRequest, Optional<Event>> {

    private static Logger logger = LoggerFactory.getLogger(CanvasPageRequestQuizShowOrIndexToNavigatedToEventConverter.class);

    @Override
    public boolean supports(CanvasPageRequest source) {
        return (source.getWebApplicationController().equalsIgnoreCase("quizzes/quizzes")) &&
                (source.getWebApplicationAction().equalsIgnoreCase("show")
                    || source.getWebApplicationAction().equalsIgnoreCase("index")) &&
                source.getHttpMthod().equalsIgnoreCase(HttpMethod.GET.toString()) &&
                source.getHttpStatus().equals(String.valueOf(HttpStatus.OK.value()));
    }

    @Override
    public Optional<Event> convert(CanvasPageRequest source, SupportingEntities supportingEntities) {

        logger.debug("Source: {}", source);

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

                Event event = null;
                Enrollment enrollment = null;

                if (source.getCourseId().isPresent()) {
                    String courseId = source.getCourseId().get().toString();
                    Optional<Enrollment> maybeEnrollment
                    = supportingEntities.getEnrollments()
                      .values().stream()
                      .filter(
                              e -> e.getKlass().getSourcedId().equalsIgnoreCase(courseId)
                                && e.getUser().getSourcedId().equalsIgnoreCase(user.getSourcedId())
                          ).findFirst();
                    
                    if (!maybeEnrollment.isPresent()) {
                      // TODO fixme
                      // we should not be relying on enrollment below
                      return Optional.empty();
                    }
                    
                    enrollment = maybeEnrollment.get();

                    Entity quizObject = null;
                    if (source.getQuizId() != null
                            && source.getQuizId().isPresent()) {

                        LineItem canvasQuizLineItem = supportingEntities.getLineItems().values().stream().filter(
                                l -> l.getSourcedId().equalsIgnoreCase(
                                        source.getQuizId().get().toString())).findFirst().get();

                        quizObject = new Entity.Builder()
                                .withId(String.valueOf(source.getQuizId().get()))
                                .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.ASSIGNABLE_DIGITAL_RESOURCE)
                                .withName(canvasQuizLineItem.getTitle())
                                .build();
                    } else {
                        quizObject = new Entity.Builder()
                                .withId(source.getUrl())
                                .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.DIGITAL_RESOURCE)
                                .build();
                    }

                    event = EventBuilderUtils.usingNavigationEventType()
                            .withObject(quizObject)
                            .withEventTime(eventTime)
                            .withAgent(usingPersonType(user, user.getUserId(), supportingEntities.getUserEmailMap().get(
                                    user.getSourcedId()), source.getRootAccountId().toString()).build())
                            .withGroup(usingCourseSectionGroup(enrollment).build())
                            .withMembership(usingMembership(enrollment).build())
                            .withFederatedSession(source.getSessionId())
                            .build();

                    result = Optional.of(event);
                } else {
                    result = Optional.empty();
                }
            } else {
                result = Optional.empty();
            }
        }

        return result;
    }

}
