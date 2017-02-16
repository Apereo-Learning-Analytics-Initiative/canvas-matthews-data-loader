package unicon.matthews.dataloader.canvas.io.converter;

import java.util.HashMap;
import java.util.Map;
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
    
    Map<String, String> metadata = new HashMap<>();
    
    String userId = source.getCanvasId().isPresent() ? String.valueOf(source.getCanvasId().get()) : null;
    
    if (StringUtils.isNotBlank(userId)) {
      metadata.put("CANVAS_USER_ID", userId);
    }
    
    String email = null;
    Map<String, String> userEmailMap = supportingEntities.getUserEmailMap();
    if (userEmailMap != null) {
      email = userEmailMap.get(String.valueOf(source.getId()));
    }
    
    User user 
      = new User.Builder()
        .withSourcedId(String.valueOf(source.getId()))
        .withUserId(String.valueOf(source.getCanvasId().get()))
        .withFamilyName(lastName)
        .withGivenName(firstName)
        .withEmail(email)
        .withMetadata(metadata)
        .build();
    
    return Optional.of(user);
  }

}
