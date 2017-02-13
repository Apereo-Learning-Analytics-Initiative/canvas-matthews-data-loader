package unicon.matthews.dataloader.canvas.io.converter;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import unicon.matthews.dataloader.canvas.model.CanvasEnrollmentDimension;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;

@Component
public class CanvasEnrollmentConverter implements Converter<CanvasEnrollmentDimension, Optional<Enrollment>> {

  @Override
  public boolean supports(CanvasEnrollmentDimension source) {
    return true;
  }

  @Override
  public Optional<Enrollment> convert(CanvasEnrollmentDimension source, SupportingEntities supportingEntities) {
    
    Map<String, Class> classes = supportingEntities.getClasses();
    Map<String, User> users = supportingEntities.getUsers();
    
    
    System.out.println("***************************");
    System.out.println(classes);
    System.out.println(users);
    System.out.println("***************************");
    
    Role role = null;
    String roleType = source.getType();
    if ("StudentEnrollment".equalsIgnoreCase(roleType) 
        || "StudentViewEnrollment".equalsIgnoreCase(roleType) 
        || "ObserverEnrollment".equalsIgnoreCase(roleType) ) {
      role = Role.student;
    }
    else {
      role = Role.teacher;
    }
    
    Status status = null;
    if ("active".equalsIgnoreCase(source.getWorkflowState())) {
      status = Status.active;
    }
    else {
      status = Status.inactive;
    }
    
    Enrollment enrollment
      = new Enrollment.Builder()
        .withSourcedId(String.valueOf(source.getId()))
        .withRole(role)
        .withStatus(status)
        .withKlass(classes.get(String.valueOf(source.getCourseSectionId().get())))
        .withUser(users.get(String.valueOf(source.getUserId().get())))
        .build();
    
    return Optional.of(enrollment);
  }

}
