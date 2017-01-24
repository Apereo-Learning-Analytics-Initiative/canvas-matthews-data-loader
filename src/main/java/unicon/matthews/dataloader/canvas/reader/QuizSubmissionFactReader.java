package unicon.matthews.dataloader.canvas.reader;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unicon.matthews.dataloader.canvas.model.CanvasDataArtifact;
import unicon.matthews.dataloader.canvas.model.CanvasDataDump;
import unicon.matthews.dataloader.canvas.model.CanvasDataFile;
import unicon.matthews.dataloader.canvas.model.CanvasQuizSubmissionFact;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class QuizSubmissionFactReader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final List<String> ARTIFACTS = Arrays.asList(
            "quiz_submission_fact",
            "quiz_submission_historical_fact");

    private CsvMapper tsvMapper;
    private CsvSchema tsvSchema;

    public QuizSubmissionFactReader() {
        // To allow for unknown trailing fields, enable the IGNORE_TRAILING_UNMAPPABLE feature below
        this.tsvMapper = new CsvMapper().configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false);
        this.tsvSchema = tsvMapper.schemaFor(CanvasQuizSubmissionFact.class).withColumnSeparator('\t');
    }

    public Collection<CanvasQuizSubmissionFact> read(CanvasDataDump dump) throws IOException {

        Collection<CanvasQuizSubmissionFact> canvasQuizSubmissionFacts = new ArrayList<>();

        List<CanvasDataArtifact> quizSubmissionArtifacts = dump.getArtifactsByTable().entrySet().stream().filter(
                artifactEntry -> ARTIFACTS.contains(artifactEntry.getKey())).map(Map.Entry::getValue).collect(
                Collectors.toList());

        List<CanvasDataFile> quizSubmissionDataFiles = quizSubmissionArtifacts.stream().map(
                CanvasDataArtifact::getFiles).flatMap(List::stream).collect(Collectors.toList());

        for (CanvasDataFile dataFile : quizSubmissionDataFiles) {
            MappingIterator<CanvasQuizSubmissionFact> iterator = tsvMapper.readerFor(
                    CanvasQuizSubmissionFact.class).with(tsvSchema).readValues(new GZIPInputStream(
                    new FileInputStream(dataFile.getDownloadPath().toFile())));
            canvasQuizSubmissionFacts.addAll(iterator.readAll());
        }

        return canvasQuizSubmissionFacts;
    }
}
