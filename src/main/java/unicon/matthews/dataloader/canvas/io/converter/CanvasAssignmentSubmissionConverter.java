package unicon.matthews.dataloader.canvas.io.converter;

import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingCourseSectionGroup;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingMembership;
import static unicon.matthews.dataloader.canvas.io.converter.EventBuilderUtils.usingPersonType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Component;

import unicon.matthews.caliper.Entity;
import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasAssignmentSubmissionDimension;
import unicon.matthews.dataloader.canvas.model.CanvasAssignmentSubmissionFact;
import unicon.matthews.dataloader.canvas.model.CanvasUserDimension;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.User;

@Component
public class CanvasAssignmentSubmissionConverter implements Converter<CanvasAssignmentSubmissionFact, Optional<Event>> {

  @Override
  public boolean supports(CanvasAssignmentSubmissionFact source) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Optional<Event> convert(CanvasAssignmentSubmissionFact source, SupportingEntities supportingEntities) {
    Optional<Event> result = null;

    CanvasAssignmentSubmissionDimension canvasAssignmentSubmissionDimension =
            supportingEntities.getCanvasAssignmentSubmissionDimensions().stream().filter(
                    dim -> source.getAssignmentId().equals(dim.getId())).findFirst().get();

    User user = supportingEntities.getUsers().get(source.getUserId().toString());

    CanvasUserDimension canvasUserDimension = supportingEntities.getCanvasUserDimensions().stream().filter(
            u -> u.getId().equalsIgnoreCase(source.getUserId().toString())).findFirst().get();

    LocalDateTime eventTime = LocalDateTime.ofInstant(canvasAssignmentSubmissionDimension.getCreatedAt().get(),
            ZoneId.of("UTC"));

    Enrollment enrollment = supportingEntities.getEnrollments().values().stream().filter(
            e -> e.getKlass().getSourcedId().equalsIgnoreCase(
                    source.getCourseId().toString())).findFirst().get();

    LineItem canvasAssignmentLineItem = supportingEntities.getLineItems().values().stream().filter(
            l -> l.getSourcedId().equalsIgnoreCase(
                canvasAssignmentSubmissionDimension.getAssignmentId().toString())).findFirst().get();

    Event event = new EventBuilderUtils()
            .usingAssignmentSubmissionEventType()
            .withEventTime(eventTime)
            .withAgent(usingPersonType(user, user.getUserId(), supportingEntities.getUserEmailMap().get(user.getSourcedId()),
                    canvasUserDimension.getRootAccountId().get().toString()).build())
            .withObject(new Entity.Builder()
                    .withId(source.getSubmissionId().toString())
                    .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.RESULT)
                    .withAssignable(new Entity.Builder()
                            .withId(canvasAssignmentSubmissionDimension.getAssignmentId().toString())
                            .withType(EventBuilderUtils.CaliperV1p1Vocab.Entity.ASSIGNABLE_DIGITAL_RESOURCE)
                            .withName(canvasAssignmentLineItem.getTitle())
                            .build())
                    .build())
            .withGroup(usingCourseSectionGroup(enrollment).build())
            .withMembership(usingMembership(enrollment).build())
            .withFederatedSession("TBD - correlate with request data")
            .build();

    return Optional.of(event);
  }

}
