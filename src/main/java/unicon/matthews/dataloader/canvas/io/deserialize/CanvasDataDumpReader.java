package unicon.matthews.dataloader.canvas.io.deserialize;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unicon.matthews.dataloader.canvas.model.CanvasDataArtifact;
import unicon.matthews.dataloader.canvas.model.CanvasDataDump;
import unicon.matthews.dataloader.canvas.model.CanvasDataFile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * Deserializes Canvas tab delimited data dump artifacts into the specified model type.
 *
 * @param <T> model type to be loaded, must implement <code>ReadableCanvasDumpArtifact</code>
 */
public class CanvasDataDumpReader<T extends ReadableCanvasDumpArtifact> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private T type;
    private Class<T> clazz;

    private CsvMapper tsvMapper;
    private CsvSchema tsvSchema;
    private Function<T, Boolean> filter;
    private List<String> includedTypes;

    public CanvasDataDumpReader(Class<T> clazz) throws Exception {
        this.type = clazz.newInstance();
        this.clazz = clazz;
        List<? extends Enum> supportedTypes = type.supports();
        this.includedTypes = supportedTypes.stream().map(Enum::name).collect(Collectors.toList());
        // To allow for unknown trailing fields, enable the IGNORE_TRAILING_UNMAPPABLE feature below
        this.tsvMapper = new CsvMapper().configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false);
        this.tsvSchema = tsvMapper.schemaFor(clazz).withColumnSeparator('\t');
    }

    public static <T extends ReadableCanvasDumpArtifact> CanvasDataDumpReader<T> forType(Class<T> clazz)
            throws Exception {
        return new CanvasDataDumpReader<>(clazz);
    }

    /**
     * Constrain the artifact types to the specified values, instead of reading all supported artifacts.
     *
     * <p>The types must be be listed in the <code>ReadableCanvasDumpArtifact</code> Type parameter Enum, and will be
     * validated at runtime.</p>
     * @param types enum types designating the artifacts to be included
     * @return reader with the supported artifact types constrained to only read the ones specified
     * @throw IllegalArgumentException if the provided artifact types are not in the supported list for the
     *         <code>ReadableCanvasDumpArtifact</code> type
     */
    public CanvasDataDumpReader<T> includeOnly(Enum... types) {
        if (!type.supports().containsAll(Arrays.asList(types))) {
            throw new IllegalArgumentException(String.format("The Canvas Data Dump reader failed to initialize " +
                    "because invalid artifact types were specified for model %s. Allowed: %s, Provided: %s",
                    clazz.getName(), type.supports().toString(), Arrays.asList(types)).toString());
        }
        includedTypes = Arrays.asList(types).stream().map(Enum::name).collect(Collectors.toList());
        return this;
    }

    /**
     * Adds a filter to only include artifacts where the provided filter expression evaluates true.
     *
     * @param filter lambda expression to filter the artifact
     * @return reader with the filter enabled
     */
    public CanvasDataDumpReader<T> withFilter(Function<T, Boolean> filter) {
        this.filter = filter;
        return this;
    }

    public Collection<T> read(CanvasDataDump dump) throws Exception {

        Collection<T> results = new ArrayList<>();

        List<CanvasDataArtifact> artifacts = dump.getArtifactsByTable().entrySet().stream().filter(
                artifactEntry -> includedTypes.contains(artifactEntry.getKey())).map(Map.Entry::getValue).collect(
                Collectors.toList());

        List<CanvasDataFile> files = artifacts.stream().map(CanvasDataArtifact::getFiles).flatMap(
                List::stream).collect(Collectors.toList());

        for (CanvasDataFile dataFile : files) {
            MappingIterator<T> iterator = tsvMapper.readerFor(clazz).with(tsvSchema).readValues(new GZIPInputStream(
                    new FileInputStream(dataFile.getDownloadPath().toFile())));
            if (filter != null) {
                while (iterator.hasNextValue()) {
                    T item = iterator.nextValue();
                    if (filter.apply(item)) {
                        results.add(item);
                    }
                }
            } else {
                results.addAll(iterator.readAll());
            }
        }

        return results;
    }
}
