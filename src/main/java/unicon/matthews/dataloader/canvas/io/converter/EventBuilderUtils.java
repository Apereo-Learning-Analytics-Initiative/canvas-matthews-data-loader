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
    // https://github.com/IMSGlobal/caliper-contexts-public/blob/public/v1/Context.json
    public static class CaliperV1p0Vocab {

        public static final String CONTEXT = "http://purl.imsglobal.org/ctx/caliper/v1/Context";

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
            public static final String MESSAGE = "http://purl.imsglobal.org/caliper/v1/Message";
            public static final String COURSE = "http://purl.imsglobal.org/caliper/v1/lis/Course";
            public static final String COURSE_OFFERING = "http://purl.imsglobal.org/caliper/v1/lis/CourseOffering";
            public static final String COURSE_SECTION = "http://purl.imsglobal.org/caliper/v1/lis/CourseSection";
        }
    }

    /**
     * https://github.com/IMSGlobal/caliper-spec/blob/master/caliper.md
     *
     * This is a more comprehensive list of Caliper the current 1.1 spec (minus roles at this time)
     *
     * Use of JSON-LD @context along with relative IRIs for Caliper 1.1 types allow them to be shortened in all examples
     * in the 1.1 spec and explicit versions (v1) have been removed (see https://github.com/IMSGlobal/caliper-spec/issues/70)
     * since it is now provided solely by the context version and was really redundant in v1.
     * See the context definition at http://purl.imsglobal.org/ctx/caliper/v1p1 for the expanded IRIs  for
     * actors/entities/objects, verbs/actions and events.
     **/
    public static class CaliperV1p1Vocab {

        public static final String CONTEXT = "http://purl.imsglobal.org/ctx/caliper/v1p1";

        public static class Entity {
            public static final String ENTITY = "Entity";
            public static final String AGENT = "Agent";
            public static final String ANNOTATION = "Annotation";
            public static final String ASSESSMENT = "Assessment";
            public static final String ASSESSMENT_ITEM = "AssessmentItem";
            public static final String ASSIGNABLE_DIGITAL_RESOURCE = "AssignableDigitalResource";
            public static final String ATTEMPT = "Attempt";
            public static final String AUDIO_OBJECT = "AudioObject";
            public static final String BOOKMARK_ANNOTATION = "BookmarkAnnotation";
            public static final String CHAPTER = "Chapter";
            // Standalone /v1/lis/Course entity has been dropped in v1.1 (offering and section already existed)
            public static final String COURSE_OFFERING = "CourseOffering"; // dropped lis in v1.1
            public static final String COURSE_SECTION = "CourseSection"; // dropped lis in v1.1
            public static final String DIGITAL_RESOURCE = "DigitalResource";
            // Renamed from Collection to DigitalResourceCollection in v1.1
            public static final String DIGITAL_RESOURCE_COLLECTION = "DigitalResourceCollection";
            public static final String DOCUMENT = "Document";
            public static final String FILL_IN_BLANK_RESPONSE = "FillinBlankResponse";
            public static final String FORUM = "Forum";
            public static final String FRAME = "Frame";
            public static final String GROUP = "Group";
            public static final String HIGHLIGHT_ANNOTATION = "HighlightAnnotation";
            public static final String IMAGE_OBJECT = "ImageObject";
            public static final String LEARNING_OBJECTIVE = "LearningObjective";
            public static final String LTI_LESSON = "LtiSession";
            public static final String MEDIA_LOCATION = "MediaLocation";
            public static final String MEDIA_OBJECT = "MediaObject";
            public static final String MEMBERSHIP = "Membership";
            public static final String MESSAGE = "Message";
            public static final String MULTIPLE_CHOICE_RESPONSE = "MultipleChoiceResponse";
            public static final String MULTIPLE_RESPONSE_RESPONSE = "MultipleResponseResponse";
            public static final String ORGANIZATION = "Organization";
            public static final String PAGE = "Page";
            public static final String PERSON = "Person";
            public static final String RESPONSE = "Response";
            public static final String RESULT = "Result";
            public static final String SELECTOR = "Selector";
            public static final String SELECT_TEXT_RESPONSE = "SelectTextResponse";
            public static final String SESSION = "Session";
            public static final String SHARED_ANNOTATION = "SharedAnnotation";
            public static final String SOFTWARE_APPLICATION = "SoftwareApplication";
            public static final String TAG_ANNOTATION = "TagAnnotation";
            public static final String TEXT_POSITION_SELECTOR = "TextPositionSelector";
            public static final String THREAD = "Thread";
            public static final String TRUE_FALSE_RESPONSE = "TrueFalseResponse";
            public static final String VIDEO_OBJECT =  "VideoObject";
            public static final String WEB_PAGE = "WebPage";
        }

        public static class Event {
            public static final String ANNOTATION_EVENT = "AnnotationEvent";
            public static final String ASSESSMENT_EVENT = "AssessmentEvent";
            public static final String ASSESSMENT_ITEM_EVENT = "AssessmentItemEvent";
            public static final String ASSIGNABLE_EVENT = "AssignableEvent";
            public static final String FORUM_EVENT = "ForumEvent";
            public static final String MEDIA_EVENT = "MediaEvent";
            public static final String MESSAGE_EVENT = "MessageEvent";
            public static final String NAVIGATION_EVENT = "NavigationEvent";
            public static final String OUTCOME_EVENT = "OutcomeEvent";
            public static final String READING_EVENT = "ReadingEvent";
            public static final String SESSION_EVENT = "SessionEvent";
            public static final String THREAD_EVENT =  "ThreadEvent";
            public static final String TOOL_USE__EVENT = "ToolUseEvent";
            public static final String VIEW_EVENT = "ViewEvent";
        }

        // Action/Verbs also dropped v1 from the IRI properly in favor of only having that in the @context
        // Shortened to relative IRIs composed of the verb: prefix and named aliases
        // "verb": "http://purl.imsglobal.org/vocab/caliper/action#"
        public static class Action {
            public static final String ABANDONED = "Abandoned";
            // Caliper v1.1. dropped the Accessed action/verb
            public static final String ACTIVATED = "Activated";
            public static final String ADDED = "Added";
            public static final String ATTACHED = "Attached";
            public static final String BOOKMARKED = "Bookmarked";
            public static final String CHANGED_RESOLUTION = "ChangedResolution";
            public static final String CHANGED_SIZE = "ChangedSize";
            public static final String CHANGED_SPEED = "ChangedSpeed";
            public static final String CHANGED_VOLUME = "ChangedVolume";
            public static final String CLASSIFIED = "Classified";
            public static final String CLOSED_POPOUT = "ClosedPopout";
            public static final String COMMENTED = "Commented";
            public static final String COMPLETED = "Completed";
            public static final String CREATED = "Created";
            public static final String DEACTIVATED = "Deactivated";
            public static final String DELETED = "Deleted";
            public static final String DESCRIBED = "Described";
            public static final String DISABLED_CLOSE_CAPTIONING = "DisabledCloseCaptioning";
            public static final String DISLIKED = "Disliked";
            public static final String ENABLED_CLOSE_CAPTIONING = "EnabledCloseCaptioning";
            public static final String ENDED = "Ended";
            public static final String ENTERED_FULL_SCREEN = "EnteredFullScreen";
            public static final String EXITED_FULL_SCREEN = "ExitedFullScreen";
            public static final String FORWARDED_TO = "ForwardedTo";
            public static final String GRADED = "Graded";
            public static final String HID = "Hid";
            public static final String HIGHLIGHTED = "Highlighted";
            public static final String IDENTIFIED = "Identified";
            public static final String JUMPED_TO = "JumpedTo";
            public static final String LIKED = "Liked";
            public static final String LINKED = "Linked";
            public static final String LOGGED_IN = "LoggedIn";
            public static final String LOGGED_OUT = "LoggedOut";
            public static final String MARKED_AS_READ = "MarkedAsRead";
            public static final String MARKED_AS_UNREAD = "MarkedAsUnread";
            public static final String MODIFIED = "Modified";
            public static final String MUTED = "Muted";
            public static final String NAVIGATEDTO = "NavigatedTo";
            public static final String OPENEDPOPOUT = "OpenedPopout";
            public static final String PAUSED = "Paused";
            public static final String POSTED = "Posted";
            public static final String QUESTIONED = "Questioned";
            public static final String RANKED = "Ranked";
            public static final String RECOMMENDED = "Recommended";
            public static final String REMOVED = "Removed";
            public static final String RESET = "Reset";
            public static final String RESTARTED = "Restarted";
            public static final String RESUMED = "Resumed";
            public static final String RETRIEVED = "Retrieved";
            public static final String REVIEWED = "Reviewed";
            public static final String REWOUND = "Rewound";
            public static final String SEARCHED = "Searched";
            public static final String SHARED = "Shared";
            public static final String SHOWED = "Showed";
            public static final String SKIPPED = "Skipped";
            public static final String STARTED = "Started";
            public static final String SUBMITTED = "Submitted";
            public static final String SUBSCRIBED = "Subscribed";
            public static final String TAGGED = "Tagged";
            public static final String TIMED_OUT = "TimedOut";
            public static final String UNMUTED = "Unmuted";
            public static final String UNSUBSCRIBED = "Unsubscribed";
            public static final String USED = "Used";
            public static final String VIEWED = "Viewed";
        }
    }

    public static Agent.Builder usingPersonType(User user, String realUserId, String userLogin, String rootAccountId) {
        return new Agent.Builder()
                .withType(CaliperV1p1Vocab.Entity.PERSON)
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
                .withType(CaliperV1p1Vocab.Entity.SOFTWARE_APPLICATION)      // Should Id actually be the root_account_id? If so, does it even make sense to have that extension with the person?
                .withExtensions(Maps.ofEntries(
                        entry("redirect_url", "REDIRECT_URL?")));         // Not sure of the usefulness of this?
    }

    public static Agent.Builder usingCanvasApplication() {
        return new Agent.Builder()
                .withId("https://canvas.instructure.com")
                .withType(CaliperV1p1Vocab.Entity.SOFTWARE_APPLICATION);
    }

    public static Group.Builder usingCourseSectionGroup(Enrollment enrollment) {
        return new Group.Builder()
                .withType(CaliperV1p1Vocab.Entity.COURSE_SECTION)
                .withId(enrollment.getKlass().getSourcedId())
                .withExtensions(Maps.ofEntries(
                        entry("context_type", enrollment.getKlass().getTitle())));  // TODO - optional and course title is likely wrong - but where do we find something else useful for it
    }

    public static Membership.Builder usingMembership(Enrollment enrollment) {
        return new Membership.Builder()
                .withId(enrollment.getSourcedId())
                .withType(CaliperV1p1Vocab.Entity.MEMBERSHIP)
                .withMember(enrollment.getUser().getUserId())              // TODO Redundant - This was intended for a member enrollment ID (if one exists) - perhaps we omit?
                .withOrganization(enrollment.getKlass().getSourcedId())    // CourseSection
                .withRoles(Arrays.asList(enrollment.getRole().name()));
    }

    public static Event.Builder usingBaseEvent() {
        return new Event.Builder()
                .withId(UUID.randomUUID().toString())
                .withContext(CaliperV1p1Vocab.CONTEXT)
                .withEdApp(usingCanvasApplication().build());
    }

    public static Event.Builder usingSessionEventType() {
        return usingBaseEvent()
                .withObject(usingAccountObject().build())
                .withType(CaliperV1p1Vocab.Event.SESSION_EVENT);
    }

    public static Event.Builder usingLoginEventType() {
        return usingSessionEventType()
                .withAction(CaliperV1p1Vocab.Action.LOGGED_IN);
    }

    public static Event.Builder usingLogoutEventType() {
        return usingSessionEventType()
                .withAction(CaliperV1p1Vocab.Action.LOGGED_OUT);
    }

    public static Event.Builder usingMessageEventType() {
        return usingBaseEvent()
                .withType(CaliperV1p1Vocab.Event.MESSAGE_EVENT);
    }
    
    public static Event.Builder usingViewedEventType() {
      return usingBaseEvent()
          .withType(CaliperV1p1Vocab.Event.VIEW_EVENT)
          .withAction(CaliperV1p1Vocab.Action.VIEWED);
    }

    public static Event.Builder usingQuizSubmissionEventType() {
        return usingBaseEvent()
                .withType(CaliperV1p1Vocab.Event.ASSESSMENT_EVENT)
                .withAction(CaliperV1p1Vocab.Action.COMPLETED);
    }
    
    public static Event.Builder usingAssignmentSubmissionEventType() {
      return usingBaseEvent()
              .withType(CaliperV1p1Vocab.Event.OUTCOME_EVENT)
              .withAction(CaliperV1p1Vocab.Action.SUBMITTED);
  }

}
