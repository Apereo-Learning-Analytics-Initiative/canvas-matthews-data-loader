package unicon.matthews.dataloader.canvas.io.converter;

import unicon.matthews.caliper.Event;

/**
 * General interface for type converters. The Type parameters &lt;S, T &gt; will allow the converters to be type safe
 * and allow them to be filtered by type for distinct use in the conversion service, futher filtered by the supports
 * method.
 */
public interface Converter<S, T> {

    boolean supports(S source);

    T convert(S source);

}
