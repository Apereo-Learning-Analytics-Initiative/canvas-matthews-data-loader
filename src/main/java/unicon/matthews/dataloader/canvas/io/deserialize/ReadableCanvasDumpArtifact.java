package unicon.matthews.dataloader.canvas.io.deserialize;

import java.util.List;

/**
 * Identifies types which represent Canvas data dump artifacts. Dump artifacts are provided in tab delimited
 * files with gzip compression.
 *
 * <p><code>ReadableCanvasDumpArtifact</code> types can leverage the generic <code>CanvasDataDumpReader</code> for
 * deserialization.</p>
 *
 * <p>Types must implement a list of the artifact names that they support.</p>
 */
public interface ReadableCanvasDumpArtifact {

    /**'
     * Returns a list of Canvas data dump artifact names that this type supports.
     * @return a list of Canvas data dump artifact names that this type supports.
     */
    List<String> supports();
}
