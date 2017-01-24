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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    public CanvasDataDumpReader(Class<T> clazz) throws Exception {
        this.type = clazz.newInstance();
        this.clazz = clazz;
        // To allow for unknown trailing fields, enable the IGNORE_TRAILING_UNMAPPABLE feature below
        this.tsvMapper = new CsvMapper().configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false);
        this.tsvSchema = tsvMapper.schemaFor(clazz).withColumnSeparator('\t');
    }

    public static <T extends ReadableCanvasDumpArtifact> CanvasDataDumpReader<T> forType(Class<T> clazz)
            throws Exception {
        return new CanvasDataDumpReader<>(clazz);
    }

    public Collection<T> read(CanvasDataDump dump) throws IOException {

        Collection<T> results = new ArrayList<>();

        List<CanvasDataArtifact> artifacts = dump.getArtifactsByTable().entrySet().stream().filter(
                artifactEntry -> type.supports().contains(artifactEntry.getKey())).map(Map.Entry::getValue).collect(
                Collectors.toList());

        List<CanvasDataFile> files = artifacts.stream().map(CanvasDataArtifact::getFiles).flatMap(
                List::stream).collect(Collectors.toList());

        for (CanvasDataFile dataFile : files) {
            MappingIterator<T> iterator = tsvMapper.readerFor(clazz).with(tsvSchema).readValues(new GZIPInputStream(
                    new FileInputStream(dataFile.getDownloadPath().toFile())));
            results.addAll(iterator.readAll());
        }

        return results;
    }
}
