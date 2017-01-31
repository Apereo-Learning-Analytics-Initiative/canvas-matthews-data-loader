package unicon.matthews.dataloader.canvas.io.converter;

import unicon.matthews.caliper.Agent;
import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.caliper.Group;
import unicon.matthews.caliper.Membership;
import unicon.matthews.dataloader.util.Maps;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.User;

import java.util.Arrays;
import java.util.UUID;

import static unicon.matthews.dataloader.util.Maps.entry;

/**
 * Supporting class to augment the {@link unicon.matthews.caliper.Event.Builder} with some default options for key
 * event types to eliminate a lot of duplication and distill the focus onto only the key variable data.
 *
 * <p>The vocabulary constants are an interim solution as we wait for IMS Caliper vocabulary constants to materialize.
 * </p>
 */
public class EventBuilderUtils {

    // Far from replicating the whole ontology tree (https://github.com/fullersr/caliper-ontology/blob/master/caliper.owl)
    // we just want to get some key groupings for these entities that cover most of what we have or may need for Canvas
    // use.
    // Some discrepancies exist in Canvas mapping docs, so leveraging constants will help to insure the data loaded
    // into Matthews is at least consistent to support analysis and not disjoint between different event types.
    public static class CaliperV1Vocab {

        public static class Actor {
            public static final String PERSON = "http://purl.imsglobal.org/caliper/v1/lis/Person";
            public static final String GROUP = "http://purl.imsglobal.org/caliper/v1/lis/Group";
            public static final String SOFTWARE_APPLICATION = "http://purl.imsglobal.org/caliper/v1/SoftwareApplication";
            public static final String ORGANIZATION = "http://purl.imsglobal.org/caliper/v1/w3c/Organization";
        }

        public static class Event {
            public static final String SESSION_EVENT = "http://purl.imsglobal.org/caliper/v1/SessionEvent";
            public static final String OUTCOME_EVENT = "http://purl.imsglobal.org/caliper/v1/OutcomeEvent";
        }

        public static class Action {
            public static final String LOGGED_IN = "http://purl.imsglobal.org/vocab/caliper/v1/action#LoggedIn";
            public static final String LOGGED_OUT = "http://purl.imsglobal.org/vocab/caliper/v1/action#LoggedOut";
            public static final String TIMED_OUT = "http://purl.imsglobal.org/vocab/caliper/v1/action#TimedOut";
            public static final String CREATED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Created";
            public static final String VIEWED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Viewed";
            public static final String SKIPPED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Skipped";
            public static final String REVIEWED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Reviewed";
            public static final String COMPLETED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Completed";
            public static final String SUBMITTED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Submitted";
            public static final String UPDATED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Updated";
            public static final String ACCESSED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Accessed";
            public static final String GRADED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Graded";
            public static final String DELETED = "http://purl.imsglobal.org/vocab/caliper/v1/action#Deleted";
        }

        public static class Entity {
            public static final String COLLECTION = "http://purl.imsglobal.org/caliper/v1/Collection";
            public static final String MEMBERSHIP = "http://purl.imsglobal.org/caliper/v1/Membership";
            public static final String ATTEMPT = "http://purl.imsglobal.org/caliper/v1/Attempt";
            public static final String DIGITAL_RESOURCE = "http://purl.imsglobal.org/caliper/v1/DigitalResource";
            public static final String ASSIGNABLE_DIGITAL_RESOURCE = "http://purl.imsglobal.org/caliper/v1/AssignableDigitalResource";
            public static final String COURSE = "http://purl.imsglobal.org/caliper/v1/lis/Course";
            public static final String COURSE_OFFERING = "http://purl.imsglobal.org/caliper/v1/lis/CourseOffering";
            public static final String COURSE_SECTION = "http://purl.imsglobal.org/caliper/v1/lis/CourseSection";
        }
    }

    public static Agent.Builder usingPersonType(User user, String realUserId, String userLogin, String rootAccountId) {
        return new Agent.Builder()
                .withType(CaliperV1Vocab.Actor.PERSON)
                .withId(user.getSourcedId())
                .withExtensions(Maps.ofEntries(
                        entry("real_user_id", realUserId),
                        entry("user_login", userLogin),
                        entry("root_account_id", rootAccountId),
                        entry("root_account_lti_guid", "TBD - Where is this?")));  // TODO - Find this data or omit
    }

    public static Entity.Builder usingAccountObject() {
        return new Entity.Builder()
                .withId("https://unicon.instructure.com")                 // TODO - Where can we pull this from dump, or will it have to be configurable?
                .withType(CaliperV1Vocab.Actor.SOFTWARE_APPLICATION)      // Should Id actually be the root_account_id? If so, does it even make sense to have that extension with the person?
                .withExtensions(Maps.ofEntries(
                        entry("redirect_url", "REDIRECT_URL?")));         // Not sure of the usefulness of this?
    }

    public static Agent.Builder usingCanvasApplication() {
        return new Agent.Builder()
                .withId("https://canvas.instructure.com")
                .withType(CaliperV1Vocab.Actor.SOFTWARE_APPLICATION);
    }

    public static Group.Builder usingCourseSectionGroup(Enrollment enrollment) {
        return new Group.Builder()
                .withType(CaliperV1Vocab.Entity.COURSE_SECTION)
                .withId(enrollment.getKlass().getSourcedId())
                .withExtensions(Maps.ofEntries(
                        entry("context_type", enrollment.getKlass().getTitle())));  // TODO - optional and course title is likely wrong - but where do we find something else useful for it
    }

    public static Membership.Builder usingMembership(Enrollment enrollment) {
        return new Membership.Builder()
                .withId(enrollment.getSourcedId())
                .withType(CaliperV1Vocab.Entity.MEMBERSHIP)
                .withMember(enrollment.getUser().getUserId())              // TODO Redundant - This was intended for a member enrollment ID (if one exists) - perhaps we omit?
                .withOrganization(enrollment.getKlass().getSourcedId())    // CourseSection
                .withRoles(Arrays.asList(enrollment.getRole().name()));
    }

    public static Event.Builder usingBaseEvent() {
        return new Event.Builder()
                .withId(UUID.randomUUID().toString())
                .withObject(usingAccountObject().build())
                .withEdApp(usingCanvasApplication().build());
    }

    public static Event.Builder usingSessionEventType() {
        return usingBaseEvent()
                .withType(CaliperV1Vocab.Event.SESSION_EVENT);

    }

    public static Event.Builder usingLoginEventType() {
        return usingSessionEventType()
                .withAction(CaliperV1Vocab.Action.LOGGED_IN);
    }

    public static Event.Builder usingLogoutEventType() {
        return usingSessionEventType()
                .withAction(CaliperV1Vocab.Action.LOGGED_OUT);
    }
}
