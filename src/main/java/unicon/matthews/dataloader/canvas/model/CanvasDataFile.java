package unicon.matthews.dataloader.canvas.model;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CanvasDataFile {

    private final String url;
    private final String filename;

    @JsonIgnore
    private Path downloadPath;

    @JsonCreator
    public CanvasDataFile(@JsonProperty("url") final String url, @JsonProperty("filename") final String filename) {
        this.url = url;
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public String getFilename() {
        return filename;
    }

    public Path getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }
}
