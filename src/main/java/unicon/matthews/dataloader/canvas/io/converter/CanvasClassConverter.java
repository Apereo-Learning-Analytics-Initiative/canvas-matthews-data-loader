package unicon.matthews.dataloader.canvas.io.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import unicon.matthews.dataloader.canvas.model.CanvasCourseSectionDimension;
import unicon.matthews.dataloader.canvas.model.CanvasEnrollmentTermDimension;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Status;

@Component
public class CanvasClassConverter implements Converter<CanvasCourseSectionDimension, Optional<unicon.matthews.oneroster.Class>> {

  @Override
  public boolean supports(CanvasCourseSectionDimension source) {
     return true;
  }

  @Override
  public Optional<Class> convert(CanvasCourseSectionDimension source, SupportingEntities supportingEntities) {
    
    String statusCode = source.getWorkflowState();
    Status status = Status.inactive;
    if (StringUtils.isNotBlank(statusCode) && statusCode.equalsIgnoreCase("active") ) {
      status = Status.active;
    }
    
    CanvasEnrollmentTermDimension enrollmentTerm = null;
    Collection<CanvasEnrollmentTermDimension> enrollmentTerms = supportingEntities.getEnrollmentTerms();
    if (enrollmentTerms != null) {
      Optional<CanvasEnrollmentTermDimension> maybeEnrollmentTerm = enrollmentTerms.stream().filter(e -> e.getId().equals(source.getEnrollmentTermId())).findFirst();
      if (maybeEnrollmentTerm.isPresent()) {
        enrollmentTerm = maybeEnrollmentTerm.get();
      }
    }
    
    Map<String, String> metadata = new HashMap<>();
    
    Optional<Instant> startAt = source.getStartAt();
    Optional<Instant> endAt = source.getEndAt();
    
    if (startAt.isPresent()) {
      LocalDateTime startDateTime = LocalDateTime.ofInstant(startAt.get(), ZoneOffset.UTC);
      LocalDate startDate = startDateTime.toLocalDate();
      metadata.put("http://unicon/vocabulary/v1/classStartDate", startDate.toString());
    }
    else if (enrollmentTerm != null && enrollmentTerm.getDateStart().isPresent()) {
      LocalDateTime startDateTime = LocalDateTime.ofInstant(enrollmentTerm.getDateStart().get(), ZoneOffset.UTC);
      LocalDate startDate = startDateTime.toLocalDate();
      metadata.put("http://unicon/vocabulary/v1/classStartDate", startDate.toString());
    }
    
    if (endAt.isPresent()) {
      LocalDateTime endDateTime = LocalDateTime.ofInstant(endAt.get(), ZoneOffset.UTC);
      LocalDate endDate = endDateTime.toLocalDate();
      metadata.put("http://unicon/vocabulary/v1/classEndDate", endDate.toString());
    }
    else if (enrollmentTerm != null && enrollmentTerm.getDateEnd().isPresent()) {
      LocalDateTime endDateTime = LocalDateTime.ofInstant(enrollmentTerm.getDateEnd().get(), ZoneOffset.UTC);
      LocalDate endDate = endDateTime.toLocalDate();
      metadata.put("http://unicon/vocabulary/v1/classEndDate", endDate.toString());
    }
    
    String classId = source.getCanvasId().isPresent() ? String.valueOf(source.getCanvasId().get()) : null;
    
    if (StringUtils.isNotBlank(classId)) {
      metadata.put("CANVAS_COURSE_SECTION_ID", classId);
    }
    
    Class klass
    = new Class
      .Builder()
        .withSourcedId(String.valueOf(source.getId()))
        .withStatus(status)
        .withTitle(source.getName())
        .withMetadata(metadata)
        .build();

    System.out.println(klass);
    
    return Optional.of(klass);
  }

}
