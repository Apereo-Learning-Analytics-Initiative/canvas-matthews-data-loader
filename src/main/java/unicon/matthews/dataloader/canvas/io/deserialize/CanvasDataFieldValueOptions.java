package unicon.matthews.dataloader.canvas.io.deserialize;

public enum CanvasDataFieldValueOptions {

    NULL("\\N");

    private final String fieldValue;

    CanvasDataFieldValueOptions(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldValue() {
        return this.fieldValue;
    }

}
