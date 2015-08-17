package classify.ListItems;

/**
 * Created by joe on 10/07/15.
 */
public class Row {

    private String rowString;
    private int rowID;
    private String fileName;

    public Row(String rowString, int rowID, String fileName) {
        this.rowString = rowString;
        this.rowID = rowID;
        this.fileName = fileName;
    }

    public String getRowString() {
        return rowString;
    }

    public void setRowString(String rowString) {
        this.rowString = rowString;
    }

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
