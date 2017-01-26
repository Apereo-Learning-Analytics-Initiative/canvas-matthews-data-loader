package unicon.matthews.dataloader.canvas.io.converter;

import org.springframework.stereotype.Component;
import unicon.matthews.caliper.Agent;
import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.caliper.Group;
import unicon.matthews.caliper.Membership;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.dataloader.util.Maps;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

import static unicon.matthews.dataloader.util.Maps.entry;

/**
 * Converts a <code>CanvasPageRequest</code> to an appropriate Matthews Caliper <code>Event</code> type for login
 * events.
 */
@Component
public class CanvasPageRequestToLoginEventConverter implements Converter<CanvasPageRequest, Event> {

    @Override
    public boolean supports(CanvasPageRequest source) {
        return source.getWebApplicationController().equalsIgnoreCase("login") &&
                source.getWebApplicationAction().equalsIgnoreCase("new");
    }

    public Event convert(CanvasPageRequest canvasPageRequest) {

        Event event = new Event.Builder()
                .withId(UUID.randomUUID().toString())
                .withType("http://purl.imsglobal.org/caliper/v1/SessionEvent")
                .withAgent(new Agent.Builder()
                        .withId(String.valueOf(canvasPageRequest.getUserId().orElse(null)))
                        .withType("http://purl.imsglobal.org/caliper/v1/Person")
                        .withExtensions(Maps.ofEntries(
                                entry("real_user_id", String.valueOf(canvasPageRequest.getRealUserId().orElse(null))),
                                entry("user_login", "TBD - Where is this - OneRoster User data?"),
                                entry("root_account_id", canvasPageRequest.getRootAccountId().toString()),
                                entry("root_account_lti_guid", "TBD - Where is this?")))
                        .build())
                .withAction("http://purl.imsglobal.org/vocab/caliper/v1/action#LoggedIn")
                .withObject(new Entity.Builder()
                        .withId("HOSTNAME?")
                        .withType("http://purl.imsglobal.org/caliper/v1/SoftwareApplication")
                        .withExtensions(Maps.ofEntries(
                                entry("redirect_url", "REDIRECT_URL?")))
                        .build())
                .withEventTime(LocalDateTime.ofInstant(canvasPageRequest.getTimestamp(), ZoneId.of("UTC")))
                .withEdApp(new Agent.Builder()
                        .withId("HOSTNAME?")
                        .withType("http://purl.imsglobal.org/caliper/v1/SoftwareApplication")
                        .build())
                .withGroup(new Group.Builder()
                        .withId(String.valueOf(canvasPageRequest.getCourseId().orElse(null)))
                        .withType("http://purl.imsglobal.org/caliper/v1/CourseSection")
                        .withExtensions(Maps.ofEntries(
                                entry("context_type", "TBD")))
                        .build())
                .withMembership(new Membership.Builder()
                        .withId("SOME_MEMBERSHIP_ID")
                        .withType("http: //purl.imsglobal.org/caliper/v1/Membership")
                        .withMember("TBD ?")
                        .withOrganization("TBD ?")
                        .withRoles(Arrays.asList("???"))
                        .build())
                .withFederatedSession(canvasPageRequest.getSessionId()) // Use FederatedSession because spec does not have a separate session field.
                .build();

        return event;
    }
}
