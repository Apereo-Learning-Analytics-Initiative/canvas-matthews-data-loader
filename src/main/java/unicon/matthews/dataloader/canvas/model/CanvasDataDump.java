package unicon.matthews.dataloader.canvas.model;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import unicon.matthews.dataloader.canvas.io.deserialize.EpochMillisecondsDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasDataDump {

    private String accountId;
    @JsonDeserialize(using = EpochMillisecondsDeserializer.class)
    private Instant expires;
    private long sequence;
    private Instant updatedAt;
    private String schemaVersion;
    private String dumpId;
    private int numFiles;
    private boolean finished;
    private Instant createdAt;
    private Map<String, CanvasDataArtifact> artifactsByTable;

    @JsonIgnore
    private Path downloadPath;

    @JsonCreator
    public CanvasDataDump(
            @JsonProperty("accountId") final String accountId,
            @JsonProperty("expires") final Instant expires,
            @JsonProperty("sequence") final long sequence,
            @JsonProperty("updatedAt") final Instant updatedAt,
            @JsonProperty("schemaVersion") final String schemaVersion,
            @JsonProperty("numFiles") final int numFiles,
            @JsonProperty("createdAt") final Instant createdAt,
            @JsonProperty("dumpId") final String dumpId,
            @JsonProperty("finished") final boolean finished,
            @JsonProperty("artifactsByTable") final Map<String, CanvasDataArtifact> artifactsByTable) {
        this.dumpId = dumpId;
        this.sequence = sequence;
        this.accountId = accountId;
        this.numFiles = numFiles;
        this.finished = finished;
        this.expires = expires;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.schemaVersion = schemaVersion;
        this.artifactsByTable = artifactsByTable;
    }

    public String getDumpId() {
        return dumpId;
    }

    public long getSequence() {
        return sequence;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getNumFiles() {
        return numFiles;
    }

    public boolean isFinished() {
        return finished;
    }

    public Instant getExpires() {
        return expires;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public Map<String, CanvasDataArtifact> getArtifactsByTable() {
        Map<String, CanvasDataArtifact> result = null;
        if (artifactsByTable != null) {
            result = Collections.unmodifiableMap(artifactsByTable);
        } else {
            result = Collections.emptyMap();
        }
        return result;
    }

    public Path getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }
}
