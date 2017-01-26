package unicon.matthews.dataloader.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Helper class for working with type safe Maps within fluent interfaces in a more seamless way. Derived from the blog
 * post <a href="http://minborgsjavapot.blogspot.com/2014/12/java-8-initializing-maps-in-smartest-way.html">
 * Java 8, Initializing Maps in the Smartest Way</a>
 *
 * <p>Statically import the {@link Maps#entry} method and use like this:
 * <pre>{@code
 * Map<String, String> map = Maps.ofEntries(
 *     entry("key1", "value1"),
 *     entry("key2", "value2"),
 *     entry("key3", "value3"));
 * }</pre>
 * </p>
 * <p></p>This is an interim step until we get the new convenience factory methods of the same name
 * (<a href="http://download.java.net/java/jdk9/docs/api/java/util/Map.html#ofEntries-java.util.Map.Entry...-">Map.ofEntries</a>)
 * being added in Java 9 Collections.
 * </p>
 *
 * @author Brad Szabo (bszabo@unicon.net)
 * @see <a href="http://minborgsjavapot.blogspot.com/2014/12/java-8-initializing-maps-in-smartest-way.html">Java 8, Initializing Maps in the Smartest Way</a>
 * @see <a href="http://download.java.net/java/jdk9/docs/api/java/util/Map.html#ofEntries-java.util.Map.Entry...-">Map.ofEntries</a>
 */
public class Maps {

    /**
     * Helper method to provide a more fluent interface to in-line Map creation. Use in conjunction with {@link #entry}.
     *
     * @param entry varargs of Map.Entry for the Map entries
     * @param <K> the output type of the key mapping function
     * @param <V> the output type of the value mapping functio
     * @return a type safe Map with the provided entries
     */
    public static <K, V> Map<K, V> ofEntries(final Entry<? extends K, ? extends V>... entry) {
        return Arrays.asList(entry).stream().map(e -> e).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    /**
     * Helper method to provide a more fluent interface to in-line Map.Entry creation.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param <K> the output type of the key mapping function
     * @param <V> the output type of the value mapping function
     * @return a type safe Map Entry with the provided key and value
     */
    public static <K, V> Entry<K, V> entry(final K key, final V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

}
