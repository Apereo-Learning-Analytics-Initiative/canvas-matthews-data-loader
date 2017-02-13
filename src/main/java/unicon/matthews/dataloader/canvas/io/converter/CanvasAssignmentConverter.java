package unicon.matthews.dataloader.canvas.io.converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import unicon.matthews.dataloader.canvas.model.CanvasAssignmentDimension;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.LineItemCategory;
import unicon.matthews.oneroster.Status;

@Component
public class CanvasAssignmentConverter implements Converter<CanvasAssignmentDimension, Optional<LineItem>> {

  @Override
  public boolean supports(CanvasAssignmentDimension source) {
    return true;
  }

  @Override
  public Optional<LineItem> convert(CanvasAssignmentDimension source, SupportingEntities supportingEntities) {

    Map<String, Class> classMap = supportingEntities.getClasses();
    
    String sourcedId = String.valueOf(source.getId());
    String title = source.getTitle();
    
    LineItemCategory lineItemCategory =new LineItemCategory.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("assignment")
      .build();
    
    String dueDateString = null;
    LocalDateTime dueDate = null;

    Optional<Instant> dueDateInstant = source.getDueAt();
    if (dueDateInstant.isPresent()) {
      dueDate = LocalDateTime.ofInstant(dueDateInstant.get(),ZoneOffset.UTC);
    }
    
    String classId = String.valueOf(source.getCourseId().get());

    Class klass = null;
    
    if (StringUtils.isNotBlank(classId)) {
      klass = classMap.get(classId);
    }
    
    LineItem lineItem
      = new LineItem.Builder()
          .withSourcedId(sourcedId)
          .withTitle(title)
          .withCategory(lineItemCategory)
          .withDueDate(dueDate)
          .withClass(klass)
          .build();
    
    return Optional.of(lineItem);
  }

}
