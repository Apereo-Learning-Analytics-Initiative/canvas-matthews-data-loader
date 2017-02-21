package unicon.matthews.dataloader.canvas.io.converter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import unicon.matthews.caliper.Agent;
import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.caliper.Group;
import unicon.matthews.caliper.Membership;
import unicon.matthews.dataloader.canvas.model.CanvasDataPseudonymDimension;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.dataloader.util.Maps;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;
import static unicon.matthews.dataloader.util.Maps.entry;

/**
 * Converts a <code>CanvasPageRequest</code> to an appropriate Matthews Caliper <code>Event</code> type for logout
 * events.
 */
@Component
public class CanvasPageRequestToLogoutEventConverter implements Converter<CanvasPageRequest, Optional<Event>> {

    @Override
    public boolean supports(CanvasPageRequest source) {
        return source.getWebApplicationController().equalsIgnoreCase("login") &&
                source.getWebApplicationAction().equalsIgnoreCase("destroy") &&
                source.getHttpStatus().equals(String.valueOf(HttpStatus.OK.value()));
    }

    @Override
    public Optional<Event> convert(CanvasPageRequest request, SupportingEntities supportingEntities) {

        Optional<Event> result = null;

        String userId = request.getUserId().get().toString();

        User user = supportingEntities.getUsers().get(userId);

        CanvasDataPseudonymDimension pseudonym = supportingEntities.getPseudonymDimensions().stream().filter(
                    p -> p.getUserId().toString().equalsIgnoreCase(userId)
            ).findFirst().get();

        String userLogin = pseudonym.getUniqueName();
        String rootAccountId = request.getRootAccountId().toString();

        LocalDateTime eventTime = LocalDateTime.ofInstant(request.getTimestamp(), ZoneId.of("UTC"));

        Event event;
        Enrollment enrollment = null;
        if (request.getCourseId().isPresent()) {
            String courseId = request.getCourseId().get().toString();
            enrollment = supportingEntities.getEnrollments().values().stream().filter(
                    e -> e.getKlass().getSourcedId().equalsIgnoreCase(courseId)).findFirst().get();

            event = EventBuilderUtils.usingLogoutEventType()
                    .withEventTime(eventTime)
                    .withAgent(usingPersonType(user, userId, userLogin, rootAccountId).build())
                    .withGroup(usingCourseSectionGroup(enrollment).build())
                    .withMembership(usingMembership(enrollment).build())
                    .withFederatedSession(request.getSessionId())
                    .build();
        } else { // Omit the optional group and membership info if we have no enrollment info
            event = EventBuilderUtils.usingLogoutEventType()
                    .withEventTime(eventTime)
                    .withAgent(usingPersonType(user, userId, userLogin, rootAccountId).build())
                    .withFederatedSession(request.getSessionId())
                    .build();
        }

        result = Optional.of(event);

        return result;
    }
}
