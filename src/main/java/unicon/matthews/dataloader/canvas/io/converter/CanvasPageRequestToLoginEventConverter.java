package unicon.matthews.dataloader.canvas.io.converter;

import org.springframework.stereotype.Component;
import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasDataPseudonymDimension;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;

/**
 * Converts a <code>CanvasPageRequest</code> to an appropriate Matthews Caliper <code>Event</code> type for login
 * events.
 */
@Component
public class CanvasPageRequestToLoginEventConverter implements Converter<CanvasPageRequest, Optional<Event>> {

    @Override
    public boolean supports(CanvasPageRequest source) {
        return (source.getWebApplicationController().equalsIgnoreCase("login/canvas")) &&
                (source.getWebApplicationAction().equalsIgnoreCase("new") ||
                        source.getWebApplicationAction().equalsIgnoreCase("create")) &&
                source.getHttpStatus().equals("200");
    }

    @Override
    public Optional<Event> convert(CanvasPageRequest request, SupportingEntities supportingEntities) {

        Optional<Event> result = null;

        Optional<String> userId = getUserIdFromAnotherSessionRequest(request, supportingEntities);

        if (!userId.isPresent()) {
            result = Optional.empty();
        } else {

            User user = supportingEntities.getUsers().values().stream().filter(u -> u.getSourcedId().equalsIgnoreCase(
                    userId.get())).findFirst().get();

            CanvasDataPseudonymDimension pseudonym = supportingEntities.getPseudonymDimensions().stream().filter(
                    p -> p.getUserId().toString().equalsIgnoreCase(userId.get())
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

                event = EventBuilderUtils.usingLoginEventType()
                        .withEventTime(eventTime)
                        .withAgent(usingPersonType(user, userId.get(), userLogin, rootAccountId).build())
                        .withGroup(usingCourseSectionGroup(enrollment).build())
                        .withMembership(usingMembership(enrollment).build())
                        .withFederatedSession(request.getSessionId())
                        .build();
            } else { // Omit the optional group and membership info if we have no enrollment info
                event = EventBuilderUtils.usingLoginEventType()
                        .withEventTime(eventTime)
                        .withAgent(usingPersonType(user, userId.get(), userLogin, rootAccountId).build())
                        .withFederatedSession(request.getSessionId())
                        .build();
            }

            result = Optional.of(event);
        }

        return result;
    }

    /**
     * The Canvas page requests data does not provide the user ID for the login event. We cannot reliably use the
     * homepage/dashboard request as that may occur far more frequently and skew login metrics, so instead we will
     * scan for another request within the authenticated session which should contain the user ID. This is horribly
     * inefficient, need to find another way.
     *
     * The users controller with the user_dashboard action sometimes appears to have a URL of /?login_success=1
     * If that was consistent we could use it and avoid this, however, our small sample of data from 1/22/2017 shows
     * only 2 with a dashboard call to url /?login_success=1, and they are the same user ID only 6 minutes apart.
     * There are 13 unique sessions that day (some clearly anonymous), but at least 8 unique authenticated sessions
     * between 4 different users, which implies there should be at least 8 successful login events. The requests
     * data is not reliable as the Canvas docs disclaim, but that would be a real loss of information.
     *
     * @param canvasPageRequest
     * @param supportingEntities
     * @return an {@code Optional} String ID for the user id, if found, otherwise the optional will be empty.
     */
    private Optional<String> getUserIdFromAnotherSessionRequest(CanvasPageRequest canvasPageRequest,
            SupportingEntities supportingEntities) {
        List<CanvasPageRequest> otherRequestsInSession = supportingEntities.getPageRequests().stream().filter(request ->
                canvasPageRequest.getSessionId().equalsIgnoreCase(request.getSessionId())
                        && request.getUserId().isPresent()).collect(Collectors.toList());

        Optional<String> userId = null;
        if (otherRequestsInSession.isEmpty()) {
            userId = Optional.empty();
        } else {
            userId = Optional.of(otherRequestsInSession.get(0).getUserId().get().toString());
        }
        return userId;
    }
}
