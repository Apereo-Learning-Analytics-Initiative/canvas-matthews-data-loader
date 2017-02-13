package unicon.matthews.dataloader.canvas.io.converter;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import unicon.matthews.dataloader.canvas.model.CanvasUserDimension;
import unicon.matthews.oneroster.User;

@Component
public class CanvasUserConverter implements Converter<CanvasUserDimension, Optional<User>> {

  @Override
  public boolean supports(CanvasUserDimension source) {
    return true;
  }

  @Override
  public Optional<User> convert(CanvasUserDimension source, SupportingEntities supportingEntities) {
    
    String lastName = null;
    String firstName = null;
    String sortableName = source.getSortableName();
    if (StringUtils.isNotBlank(sortableName)) {
      lastName = StringUtils.substringBefore(sortableName, ",");
      firstName = StringUtils.substringAfter(sortableName, ", ");
    }
    
    User user 
      = new User.Builder()
        .withSourcedId(String.valueOf(source.getId()))
        .withUserId(String.valueOf(source.getCanvasId().get()))
        .withFamilyName(lastName)
        .withGivenName(firstName)
        .build();
    
    return Optional.of(user);
  }

}
