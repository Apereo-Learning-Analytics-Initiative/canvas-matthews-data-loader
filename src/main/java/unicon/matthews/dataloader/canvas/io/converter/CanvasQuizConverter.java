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
import unicon.matthews.dataloader.canvas.model.CanvasQuizDimension;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.LineItemCategory;
import unicon.matthews.oneroster.Status;

@Component
public class CanvasQuizConverter implements Converter<CanvasQuizDimension, Optional<LineItem>> {

  @Override
  public boolean supports(CanvasQuizDimension source) {
    return true;
  }

  @Override
  public Optional<LineItem> convert(CanvasQuizDimension source, SupportingEntities supportingEntities) {
    
    Map<String, Class> classMap = supportingEntities.getClasses();
    
    String sourcedId = String.valueOf(source.getId());
    String title = source.getName();
    LineItemCategory lineItemCategory =new LineItemCategory.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("quiz")
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
