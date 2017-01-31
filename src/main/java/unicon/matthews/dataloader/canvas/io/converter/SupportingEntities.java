package unicon.matthews.dataloader.canvas.io.converter;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import unicon.matthews.dataloader.canvas.model.CanvasDataPseudonymDimension;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;
import unicon.matthews.oneroster.Class;
import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.User;

import java.util.Collection;
import java.util.Map;

@Data
@Builder
public class SupportingEntities {

    Map<String, Class> classes;
    Map<String, User> users;
    Collection<CanvasDataPseudonymDimension> pseudonymDimensions;
    Map<String, Enrollment> enrollments;
    Map<String, LineItem> lineItems;
    Collection<CanvasPageRequest> pageRequests;

}
