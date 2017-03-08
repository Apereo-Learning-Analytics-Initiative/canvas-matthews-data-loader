package unicon.matthews.dataloader.canvas.io.converter;

import org.springframework.stereotype.Component;
import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasDataPseudonymDimension;
import unicon.matthews.dataloader.canvas.model.CanvasDiscussionForumEntryDimension;
import unicon.matthews.dataloader.canvas.model.CanvasDiscussionForumEntryFact;
import unicon.matthews.dataloader.canvas.model.CanvasUserDimension;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;

/**
 * Converts a <code>CanvasDiscussionForumEntryFact</code> and its related <code>CanvasDiscussionForumEntryDimension</code>
 * to an appropriate Matthews Caliper <code>Event</code> type for forum entry events.
 */
@Component
public class CanvasDiscussionForumEntryToCaliperEventConverter implements Converter<CanvasDiscussionForumEntryFact, Optional<Event>> {

    @Override
    public boolean supports(CanvasDiscussionForumEntryFact source) {
        return true;
    }

    private static final String CANVAS_ACTIVE_STATE = "active";
    private static final String CANVAS_DELETED_STATE = "deleted";

    @Override
    public Optional<Event> convert(CanvasDiscussionForumEntryFact discussionForumEntryFact,
            SupportingEntities supportingEntities) {

        Optional<Event> result = null;

        CanvasDiscussionForumEntryDimension discussionForumEntryDimension =
                supportingEntities.getDiscussionForumEntryDimensions().stream().filter(
                        dim -> discussionForumEntryFact.getDiscussionEntryId().equals(dim.getId())).findFirst().get();

        User user = supportingEntities.getUsers().get(discussionForumEntryFact.getUserId().toString());

        CanvasUserDimension canvasUserDimension = supportingEntities.getCanvasUserDimensions().stream().filter(
                u -> u.getId().equalsIgnoreCase(discussionForumEntryFact.getUserId().toString())).findFirst().get();

        LocalDateTime eventTime = LocalDateTime.ofInstant(discussionForumEntryDimension.getCreatedAt(), ZoneId.of("UTC"));

        Enrollment enrollment 
          = supportingEntities.getEnrollments()
            .values().stream()
            .filter(
                    e -> e.getKlass().getSourcedId().equalsIgnoreCase(discussionForumEntryFact.getCourseId().toString())
                      && e.getUser().getSourcedId().equalsIgnoreCase(user.getSourcedId())
                ).findFirst().get();

        Event event = EventBuilderUtils.usingMessageEventType()
                .withAction(EventBuilderUtils.CaliperV1p1Vocab.Action.POSTED)
                .withEventTime(eventTime)
                .withAgent(usingPersonType(user, user.getUserId(), supportingEntities.getUserEmailMap().get(
                        user.getSourcedId()), canvasUserDimension.getRootAccountId().get().toString()).build())
                .withObject(new Entity.Builder()
                        .withId(discussionForumEntryDimension.getId().toString())
                        .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.MESSAGE)
                        .withIsPartOf(new Entity.Builder()
                                .withId(discussionForumEntryFact.getTopicId().toString())
                                .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.THREAD)
                                .withIsPartOf(new Entity.Builder()
                                        .withId("TBD - Forum ID?")
                                        .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.FORUM)
                                        .build())
                                .build())
                        .withDateCreated(eventTime)
                        // TODO Activity Mapping has isReplyTo section, but that is not in spec anywhere (even 1.1)
                        // Also has note "If Message is not a reply, then replyTo property is NULL."
                        // https://github.com/IMSGlobal/caliper-spec/blob/master/caliper.md#forumProfile
                        // replyTo is part of spec, and would need to be added to Matthews Event API.
                        .build())
                .withGroup(usingCourseSectionGroup(enrollment).build())
                .withMembership(usingMembership(enrollment).build())
                .withFederatedSession("TBD - correlate with request data")
                .build();

        // if (CANVAS_DELETED_STATE.equalsIgnoreCase(discussionForumEntryFact.getWorkflowState())) {
            // If deleted, should we capture and send deleted events too?
            // TODO - This is an instance of a one to many mapping of dimension/fact to multiple events.
            // eventTime = LocalDateTime.ofInstant(discussionForumEntryFact.getDeletedAt().get(), ZoneId.of("UTC"));
        // }

        result = Optional.of(event);

        return result;
    }
}
