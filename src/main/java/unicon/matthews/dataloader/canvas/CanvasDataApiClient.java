package unicon.matthews.dataloader.canvas;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import unicon.matthews.dataloader.canvas.exception.CanvasDataConfigurationException;
import unicon.matthews.dataloader.canvas.exception.UnexpectedApiResponseException;
import unicon.matthews.dataloader.canvas.model.CanvasDataArtifact;
import unicon.matthews.dataloader.canvas.model.CanvasDataDump;
import unicon.matthews.dataloader.canvas.model.CanvasDataFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Instructure Canvas Data API client. Provides access to Canvas account data dump information and downloadable data
 * files.
 * <p>Note that the Canvas Data API includes any query parameters in the signature token (in alphabetical order by key),
 * so they must be ordered properly if used.</p>
 *
 * @see <a href="https://portal.inshosteddata.com/docs/api">https://portal.inshosteddata.com/docs/api</a>
 */
@Component
public class CanvasDataApiClient {

    @Value("${canvas.baseurl:portal.inshosteddata.com}")
    private String canvasBaseUrl;

    @Value("${canvas.apikey}")
    private String canvasApiKey;

    @Value("${canvas.apisecret}")
    private String canvasApiSecret;

    /**
     * Base directory for storing downloaded Canvas dump data. Each daily dump is stored as a dated subdirectory of this
     * location.
     */
    @Value("${downloads.root.directory:CANVAS_DUMPS}")
    private String downloadsRootDirectory;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Returns the Canvas account metadata dumps for the specified period, and optionally downloads all of the dump
     * artifacts.
     *
     * @param startDate first day of the dumps to include, inclusive (Time zone UTC)
     * @param endDate last day of the dumps to include, inclusive (Time zone UTC)
     * @param options Canvas data dump fetch options such as whether to download its supporting artifacts
     * @return Canvas dump metadata
     * @throws CanvasDataConfigurationException
     * @throws UnexpectedApiResponseException
     */
    public List<CanvasDataDump> getDumps(final LocalDate startDate, final LocalDate endDate, Options options)
            throws IOException, CanvasDataConfigurationException, UnexpectedApiResponseException {

        String queryString = determineDumpsQueryStringFromDateRange(startDate, endDate);

        List<CanvasDataDump> dumps = getResources("/api/account/self/dump", queryString,
                CanvasDataDump[].class);

        dumps.forEach(dump -> { updateDumpDownloadDirectories(dump); });

        if (options.isArtifactDetailsIncluded()) {
            dumps = getDetailedDumps(dumps);
        }

        if (options.isArtifactDownloadsEnabled()) {
            download(dumps);
        }

        return dumps;
    }

    /**
     * Returns the Canvas account metadata dump for the specified date, and optionally downloads all of the dump
     * artifacts.
     *
     * @param date Canvas data dump date (Time zone UTC)
     * @param options Canvas data dump fetch options such as whether to download its supporting artifacts. Artifact
     *                details are always included with this call, even if not explicitly specified with options to be
     *                consistent with {@code #getLatestDump} which uses a different API that always returns the
     *                details.
     * @return the Canvas account metadata dump for the specified identifier
     * @throws IOException
     * @throws CanvasDataConfigurationException
     * @throws UnexpectedApiResponseException
     */
    public CanvasDataDump getDump(final LocalDate date, final Options options)
            throws IOException, CanvasDataConfigurationException, UnexpectedApiResponseException {

        String queryString = determineDumpsQueryStringFromDateRange(date, date);

        List<CanvasDataDump> dumps = getResources("/api/account/self/dump", queryString,
                CanvasDataDump[].class);

        if (dumps.size() != 1) {
            throw new IllegalStateException(String.format("Canvas data dump for %tF could not be retrieved using " +
                    "query string '%s' (Result contained %d dumps)", date, queryString, dumps.size()));
        }

        CanvasDataDump dump = dumps.get(0);

        updateDumpDownloadDirectories(dump);

        // Always fetch the details to be consistent with the getLatestDump
        dump = getDetailedDumps(Arrays.asList(dump)).get(0);

        if (options.isArtifactDownloadsEnabled()) {
            download(dump);
        }

        return dump;
    }

    /**
     * Returns the Canvas account metadata dump for the specified identifier, and optionally downloads all of the dump
     * artifacts.
     *
     * @param id Canvas data dump identifier
     * @param options Canvas data dump fetch options such as whether to download its supporting artifacts. Artifact
     *                details are always included with this call, even if not explicitly specified with options.
     * @return the Canvas account metadata dump for the specified identifier
     * @throws IOException
     * @throws CanvasDataConfigurationException
     * @throws UnexpectedApiResponseException
     */
    public CanvasDataDump getDump(final String id, final Options options)
            throws IOException, CanvasDataConfigurationException, UnexpectedApiResponseException {
        CanvasDataDump dump = getResource(String.format("/api/account/self/file/byDump/%s", id),
                CanvasDataDump.class);
        updateDumpDownloadDirectories(dump);
        if (options.isArtifactDownloadsEnabled()) {
            download(dump);
        }
        return dump;
    }

    /**
     * Returns the latest Canvas account dump metadata, and optionally downloads all of the dump artifacts.
     *
     * @param options Canvas data dump fetch options such as whether to download its supporting artifacts
     * @return the latest Canvas account metadata dump
     * @throws CanvasDataConfigurationException
     * @throws UnexpectedApiResponseException
     */
    public CanvasDataDump getLatestDump(final Options options) throws IOException, CanvasDataConfigurationException,
            UnexpectedApiResponseException {
        CanvasDataDump dump = getResource("/api/account/self/file/latest", CanvasDataDump.class);
        updateDumpDownloadDirectories(dump);
        if (options.isArtifactDownloadsEnabled()) {
            download(dump);
        }
        return dump;
    }

    /**
     * The Canvas Data API returns incomplete dump metadata when a collection of dumps is requested, so this supporting
     * method expands the dump metadata.
     *
     * @param thinDumps Canvas data dumps which lack artifact information
     * @return new collection of Canvas data dumps with full artifact information
     * @throws IOException
     * @throws CanvasDataConfigurationException
     * @throws UnexpectedApiResponseException
     */
    protected List<CanvasDataDump> getDetailedDumps(final List<CanvasDataDump> thinDumps) throws IOException,
            CanvasDataConfigurationException, UnexpectedApiResponseException{

        List<CanvasDataDump> detailedDumps = new ArrayList<>();
        CanvasDataDump detailedDump = null;

        for (CanvasDataDump thinDump : thinDumps) {
            detailedDump = getDump(thinDump.getDumpId(), Options.NONE);
            updateDumpDownloadDirectories(detailedDump);
            detailedDumps.add(detailedDump);
        }

        return detailedDumps;
    }

    protected <T> T getResource(final String resourcePath, final Class<T> responseClazz)
            throws CanvasDataConfigurationException, UnexpectedApiResponseException {
        return this.getResponseEntity(resourcePath, responseClazz).getBody();
    }

    protected <T> T getResource(final String resourcePath, final String queryParams, final Class<T> responseClazz)
            throws CanvasDataConfigurationException, UnexpectedApiResponseException {
        return this.getResponseEntity(resourcePath, queryParams, responseClazz).getBody();
    }

    protected <T> ResponseEntity<T> getResponseEntity(final String resourcePath, final Class<T> responseClazz)
            throws CanvasDataConfigurationException, UnexpectedApiResponseException {
        return this.getResponseEntity(resourcePath, "", responseClazz);
    }

    /**
     *
     * @param resourcePath API resource path
     * @param queryParams in the format key1=value1&key2=value2 - sorted by key alphabetically, or empty string
     * @param responseClazz response entity type
     * @return <code>ResponseEntity</code> containing the converted response type
     * @throws CanvasDataConfigurationException
     * @throws UnexpectedApiResponseException
     */
    protected <T> ResponseEntity<T> getResponseEntity(final String resourcePath, final String queryParams,
            final Class<T> responseClazz)
            throws CanvasDataConfigurationException, UnexpectedApiResponseException {

        HttpHeaders headers = buildHeaders(resourcePath, queryParams, HttpMethod.GET);
        final String url = buildRequestUrl(resourcePath, queryParams);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseClazz);

        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new UnexpectedApiResponseException(responseEntity.getStatusCodeValue(), url);
        }

        return responseEntity;
    }

    protected <T> List<T> getResources(final String resourcePath, final String queryParams,
            final Class<T[]> responseClazz)
            throws CanvasDataConfigurationException, UnexpectedApiResponseException {
        return getResponseEntities(resourcePath, queryParams, responseClazz).getBody();
    }

    protected <T> ResponseEntity<List<T>> getResponseEntities(final String resourcePath, final String queryParams,
            final Class<T[]> responseClazz)
            throws CanvasDataConfigurationException, UnexpectedApiResponseException {

        HttpHeaders headers = buildHeaders(resourcePath, queryParams, HttpMethod.GET);
        final String url = buildRequestUrl(resourcePath, queryParams);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<T[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseClazz);

        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new UnexpectedApiResponseException(responseEntity.getStatusCodeValue(), url);
        }

        List<T> responseBodyList = Arrays.asList(responseEntity.getBody());
        ResponseEntity<List<T>> response = new ResponseEntity<List<T>>(responseBodyList, responseEntity.getHeaders(),
                responseEntity.getStatusCode());

        return response;
    }

    private String determineDumpsQueryStringFromDateRange(final LocalDate startDate, final LocalDate endDate)
            throws IOException, CanvasDataConfigurationException, UnexpectedApiResponseException {

        ZonedDateTime currentDate = ZonedDateTime.now(ZoneOffset.UTC.normalized());
        ZonedDateTime startDateUTC = ZonedDateTime.of(startDate, LocalTime.MIDNIGHT, ZoneOffset.UTC.normalized());
        ZonedDateTime endDateUTC = ZonedDateTime.of(endDate, LocalTime.MIDNIGHT, ZoneOffset.UTC.normalized());
        if (endDateUTC.isAfter(currentDate)) {
            throw new RuntimeException(String.format("End date for the dump range must not exceed today's date. " +
                    "Invalid date provided: %tF", endDateUTC));
        }
        if (endDateUTC.isBefore(startDateUTC)) {
            throw new RuntimeException(String.format("End date for the dump range must be the same or after the " +
                    "start date. Invalid date range provided. Start: %tF, End: %tF", startDateUTC, endDateUTC));
        }
        Duration dumpsPeriod = Duration.between(startDateUTC, endDateUTC);
        long numberOfDumps = dumpsPeriod.toDays() + 1; // To be inclusive of the end date
        if (numberOfDumps > 100) {
            throw new RuntimeException(String.format("Canvas appears to only support up to 100 response items, " +
                    "returning an HTTP 400 response if exceeded. Please reduce your request range. Current " +
                    "request included %d days", numberOfDumps));
        }

        CanvasDataDump latestDump = getLatestDump(Options.NONE); // Dumps typically occur between midnight and 2am UTC
        long latestSequenceNumber = latestDump.getSequence();
        Instant latestDumpCreatedDate = latestDump.getCreatedAt();
        ZonedDateTime latestDumpDateUTC = latestDumpCreatedDate.atZone(ZoneOffset.UTC);
        ZonedDateTime firstSequenceDumpDateMidnightUTC = latestDumpDateUTC.minusDays(
                latestSequenceNumber - 1).truncatedTo(ChronoUnit.DAYS);

        if (startDateUTC.isBefore(firstSequenceDumpDateMidnightUTC)) {
            throw new RuntimeException(String.format("The requested canvas dump range exceeds what is available. " +
                    "There are currently %d dumps in the sequence. Unless there is more than one dump per day " +
                    "(which should not be the case), your initial dump was made on %tF. Please adjust your start date " +
                    "accordingly.", latestSequenceNumber, firstSequenceDumpDateMidnightUTC));
        }

        if (endDateUTC.isAfter(latestDumpDateUTC)) {
            throw new RuntimeException(String.format("The latest canvas dump available is for %tF, so please adjust " +
                    "your end date accordingly.", latestDumpDateUTC));
        }

        Duration dumpStartOffsetPeriod = Duration.between(firstSequenceDumpDateMidnightUTC, startDateUTC);
        long startAfterSequenceNumber = dumpStartOffsetPeriod.toDays();

        return String.format("after=%d&limit=%d", startAfterSequenceNumber, numberOfDumps);
    }

    private HttpHeaders buildHeaders(final String resourcePath, final String queryParams, final HttpMethod httpMethod)
            throws CanvasDataConfigurationException {

        final TimeZone tz = TimeZone.getTimeZone("GMT");
        final DateFormat df = new SimpleDateFormat("E, dd MMM Y HH:mm:ss z");
        df.setTimeZone(tz);
        final Date now = new Date();

        final String hostHeader = canvasBaseUrl;
        final String contentTypeHeader = "";
        final String md5Header = "";
        final String dateHeader = df.format(now);

        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add(httpMethod.name()).add(hostHeader).add(contentTypeHeader).add(md5Header);
        joiner.add(resourcePath).add(queryParams).add(dateHeader).add(canvasApiSecret);
        final String message = joiner.toString();

        final Mac hmac;
        try {
            hmac = Mac.getInstance("HmacSHA256");
        } catch (final NoSuchAlgorithmException e) {
            throw new CanvasDataConfigurationException(e);
        }
        final SecretKeySpec secretKeySpec = new SecretKeySpec(canvasApiSecret.getBytes(), "HmacSHA256");
        try {
            hmac.init(secretKeySpec);
        } catch (final InvalidKeyException e) {
            throw new CanvasDataConfigurationException(e);
        }
        final byte[] digest = hmac.doFinal(message.getBytes());
        final String signature = Base64.getEncoder().encodeToString(digest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("HMACAuth %s:%s", canvasApiKey, signature));
        headers.add("Date", dateHeader);

        return headers;
    }

    private String buildRequestUrl(final String resourcePath, final String queryParams) {
        String queryString = queryParams;
        if (StringUtils.isNotBlank(queryString)) {
            // The ? character is not included in the HMAC signature, so we exclude it from the sorted queryParams
            queryString = String.format("?%s", queryString);
        }
        return String.format("https://%s%s%s", canvasBaseUrl, resourcePath, queryString);
    }

    private void download(final List<CanvasDataDump> dumps) {
        dumps.forEach(dump -> {
            try {
                download(dump);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void download(final CanvasDataDump dump) throws IOException, CanvasDataConfigurationException,
            UnexpectedApiResponseException {

        if (dump.getArtifactsByTable() == null) {
            throw new IllegalStateException(String.format("Attempt to download artifacts for Canvas data dump ID '%s' " +
                    "(sequence %d) for date '%tF' failed because the dump metadata is incomplete. Request a " +
                    "detailed dump list or an individual dump to get the full information in order to be able " +
                    "to download artifacts", dump.getDumpId(), dump.getSequence(),
                    dump.getCreatedAt().atZone(ZoneOffset.UTC)));
        }

        Files.createDirectories(dump.getDownloadPath());

        Files.write(Paths.get(dump.getDownloadPath().toString(), "dump-metadata.json"),
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(dump));

        for (final String table : dump.getArtifactsByTable().keySet()) {
            final CanvasDataArtifact artifact = dump.getArtifactsByTable().get(table);
            Files.createDirectories(artifact.getDownloadPath());
            download(artifact);
        }
    }

    private void download(final CanvasDataArtifact artifact) throws IOException,
            UnexpectedApiResponseException {

        if (artifact.getDownloadPath() == null) {
            throw new IllegalStateException(String.format("Download path has not been specified for the Canvas " +
                    "artifact %s", artifact.getTableName()));
        }

        for (final CanvasDataFile dataFile : artifact.getFiles()) {
            download(dataFile);
        }
    }

    private void download(final CanvasDataFile dataFile)
            throws IOException, UnexpectedApiResponseException {

        RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        ResponseExtractor<ResponseEntity<Void>> streamingResponseExtractor = response -> {
            if (response.getStatusCode() == HttpStatus.OK) {
                Files.copy(response.getBody(), dataFile.getDownloadPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return new ResponseEntity<Void>(response.getHeaders(), response.getStatusCode());
        };

        ResponseEntity<Void> responseEntity = restTemplate.execute(URI.create(dataFile.getUrl()), HttpMethod.GET, requestCallback,
                streamingResponseExtractor);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            new UnexpectedApiResponseException(responseEntity.getStatusCodeValue(), dataFile.getUrl());
        }
    }

    private CanvasDataDump updateDumpDownloadDirectories(final CanvasDataDump dump) {
        Path dumpDirectory = Paths.get(downloadsRootDirectory, String.format("%tF", dump.getCreatedAt().atZone(
                ZoneOffset.UTC)));
        dump.setDownloadPath(dumpDirectory);
        dump.getArtifactsByTable().values().stream().forEach(artifact -> {
            artifact.setDownloadPath(Paths.get(dump.getDownloadPath().toString(), artifact.getTableName()));
            artifact.getFiles().stream().forEach(file -> file.setDownloadPath(
                    Paths.get(artifact.getDownloadPath().toString(), file.getFilename())));
        });
        return dump;
    }

    public static class Options {

        private boolean artifactDetailsIncluded;
        private boolean artifactDownloadsEnabled;

        public static Options NONE = new Options();

        public static Builder builder() {
            return Builder.instanceOf();
        }

        public static class Builder {

            private Options options = new Options();

            public static Builder instanceOf() {
                return new Builder();
            }

            /**
             * Specify to retrieve the details of all artifacts of the data dump. Canvas data omits these details by
             * default when multiple dumps are requested.
             * @return Builder to chain options
             */
            public Builder withArtifactDetails() {
                options.artifactDetailsIncluded =  true;
                return this;
            }

            /**
             * Specify to download all artifacts of the data dump. This automatically implies artifact details are
             * included.
             * @return Builder to chain options
             */
            public Builder withArtifactDownloads() {
                options.artifactDownloadsEnabled = true;
                return withArtifactDetails();
            }

            public Options build() {
                return options;
            }
        }

        public boolean isArtifactDetailsIncluded() {
            return artifactDetailsIncluded;
        }

        public boolean isArtifactDownloadsEnabled() {
            return artifactDownloadsEnabled;
        }
    }
}
