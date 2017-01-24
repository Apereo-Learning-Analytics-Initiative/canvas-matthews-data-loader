package unicon.matthews.dataloader.canvas.util;

public enum CanvasDataFieldValueOptions {

    NULL("\\N");

    private final String fieldValue;

    private CanvasDataFieldValueOptions(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldValue() {
        return this.fieldValue;
    }

}
