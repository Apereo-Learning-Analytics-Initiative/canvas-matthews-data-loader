package unicon.matthews.dataloader.canvas.io.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unicon.matthews.caliper.Event;
import unicon.matthews.dataloader.canvas.model.CanvasPageRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class CanvasConversionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<Converter<CanvasPageRequest, Optional<Event>> >pageRequestToEventConverters;

    public List<Event> convertPageRequests(Collection<CanvasPageRequest> sourceItems,
            SupportingEntities supportingEntities) {

        List<Event> events = new ArrayList<>();

        Optional<Event> event = null;

        for (CanvasPageRequest sourceItem : sourceItems) {

            Optional<Converter<CanvasPageRequest, Optional<Event>>> selectedConverter =
                    pageRequestToEventConverters.stream().filter(converter -> converter.supports(sourceItem)).findFirst();

            if (selectedConverter.isPresent()) {

                event = selectedConverter.get().convert(sourceItem, supportingEntities);

                if (event.isPresent()) {
                    events.add(event.get());
                    logger.debug("Page Request Conversion PROCESSED by converter {} : From {} > EVENT: {}",
                            selectedConverter.get().getClass().getSimpleName(), sourceItem.toString(),
                            event.get().toString());
                } else {
                    logger.debug("Page Request Conversion PROCESSED by converter {} : From {} > NO EVENT",
                            selectedConverter.get().getClass().getSimpleName(), sourceItem.toString());
                }
            } else {
                logger.debug("Page Request Conversion SKIP request with no matching converter: {}", sourceItem.toString());
            }
        }

        return events;
    }



}
