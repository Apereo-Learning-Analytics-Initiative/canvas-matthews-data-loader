package unicon.matthews.dataloader.canvas.io.converter;

import org.springframework.stereotype.Component;
import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.caliper.Group;
import unicon.matthews.caliper.Membership;
import unicon.matthews.dataloader.canvas.model.CanvasQuizSubmissionDimension;
import unicon.matthews.dataloader.canvas.model.CanvasQuizSubmissionFact;
import unicon.matthews.dataloader.canvas.model.CanvasUserDimension;
import unicon.matthews.dataloader.util.Maps;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;
import static unicon.matthews.dataloader.util.Maps.entry;

/**
 * Converts a <code>CanvasQuizSubmission</code> to a Matthews Caliper <code>Event</code> type.
 */
@Component
public class CanvasQuizSubmissionEventConverter implements Converter<CanvasQuizSubmissionFact, Optional<Event>> {

    @Override
    public boolean supports(CanvasQuizSubmissionFact source) {
        return true;
    }

    @Override
    public Optional<Event> convert(CanvasQuizSubmissionFact canvasQuizSubmissionFact, SupportingEntities supportingEntities) {

        Optional<Event> result = null;

        CanvasQuizSubmissionDimension canvasQuizSubmissionDimension =
                supportingEntities.getCanvasQuizSubmissionDimensions().stream().filter(
                        dim -> canvasQuizSubmissionFact.getQuizSubmissionId().equals(dim.getId())).findFirst().get();

        User user = supportingEntities.getUsers().get(canvasQuizSubmissionFact.getUserId().toString());

        CanvasUserDimension canvasUserDimension = supportingEntities.getCanvasUserDimensions().stream().filter(
                u -> u.getId().equalsIgnoreCase(canvasQuizSubmissionFact.getUserId().toString())).findFirst().get();

        // CanvasQuizSubmissionDimension (latest submissions) seem to always have a created date, only historical
        // dimensions sometimes do not, so not guarding against it.
        LocalDateTime eventTime = LocalDateTime.ofInstant(canvasQuizSubmissionDimension.getCreatedAt().get(),
                ZoneId.of("UTC"));

        Enrollment enrollment = supportingEntities.getEnrollments().values().stream().filter(
                e -> e.getKlass().getSourcedId().equalsIgnoreCase(
                        canvasQuizSubmissionFact.getCourseId().toString())).findFirst().get();

        LineItem canvasQuizLineItem = supportingEntities.getLineItems().values().stream().filter(
                l -> l.getSourcedId().equalsIgnoreCase(
                        canvasQuizSubmissionFact.getQuizId().toString())).findFirst().get();

        Event event = new EventBuilderUtils()
                .usingQuizSubmissionEventType()
                .withEventTime(eventTime)
                .withAgent(usingPersonType(user, user.getUserId(), supportingEntities.getUserEmailMap().get(user.getSourcedId()),
                        canvasUserDimension.getRootAccountId().get().toString()).build())
                .withObject(new Entity.Builder()
                        .withId(canvasQuizSubmissionFact.getSubmissionId().toString())
                        .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.ATTEMPT)
                        .withAssignable(new Entity.Builder()
                                .withId(canvasQuizSubmissionFact.getQuizId().toString())
                                .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.ASSIGNABLE_DIGITAL_RESOURCE)
                                .withName(canvasQuizLineItem.getTitle())
                                .build())
                        .withExtensions(Maps.ofEntries(
                                entry("count", canvasQuizSubmissionFact.getTotalAttempts().toString())))
                        .build())
                .withGroup(usingCourseSectionGroup(enrollment).build())
                .withMembership(usingMembership(enrollment).build())
                .withFederatedSession("TBD - correlate with request data")
                .build();

        return Optional.of(event);
    }
}
