package unicon.matthews.dataloader.canvas.io.deserialize;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

/**
 * Identifies types which represent Canvas data dump artifacts. Dump artifacts are provided in tab delimited
 * files with gzip compression.
 *
 * <p><code>ReadableCanvasDumpArtifact</code> types can leverage the generic <code>CanvasDataDumpReader</code> for
 * deserialization.</p>
 *
 * <p>Types must implement a list of the artifact names that they support as an Enum which is provided as a Type
 * parameter.</p>
 */
public interface ReadableCanvasDumpArtifact<T extends Enum> {

    /**'
     * Returns a list of Canvas data dump artifact names that this type supports.
     * @return a list of Canvas data dump artifact names that this type supports.
     */
    default List<? extends Enum> supports() {
        ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericInterfaces()[0];
        String typeName = parameterizedType.getActualTypeArguments()[0].getTypeName();
        T[] enumValues = null;
        try {
            Class<T> clazz = (Class<T>) Class.forName(typeName);
            enumValues = clazz.getEnumConstants();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(enumValues);
    }
}
