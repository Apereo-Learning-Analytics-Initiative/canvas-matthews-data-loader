package unicon.matthews.dataloader.canvas.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CanvasDataArtifact {

    private final String tableName;
    private final boolean partial;
    private final List<CanvasDataFile> files;

    @JsonIgnore
    private Path downloadPath;

    @JsonCreator
    public CanvasDataArtifact(
            @JsonProperty("tableName") final String tableName,
            @JsonProperty("partial") final boolean partial,
            @JsonProperty("files") final List<CanvasDataFile> files) {
        this.tableName = tableName;
        this.partial = partial;
        this.files = files;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isPartial() {
        return partial;
    }

    public List<CanvasDataFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public Path getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }
}
