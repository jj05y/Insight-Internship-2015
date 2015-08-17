package classify.ListItems;

/**
 * Created by joe on 22/07/15.
 */
public class ItemForVerification {

    int RowID;
    String name;
    int actualLabel;
    int predictedLabel;
    int rep;
    int exercise;
    String fileName;

    public ItemForVerification(int rowID, String name, int actualLabel, int predictedLabel, String fileName, int rep, int exercise) {
        RowID = rowID;
        this.name = name;
        this.actualLabel = actualLabel;
        this.predictedLabel = predictedLabel;
        this.fileName = fileName;
        this.rep = rep;
        this.exercise = exercise;
    }

    public int getExercise() {
        return exercise;
    }

    public void setExercise(int exercise) {
        this.exercise = exercise;
    }

    public ItemForVerification() {
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public int getRowID() {
        return RowID;
    }

    public void setRowID(int rowID) {
        RowID = rowID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActualLabel() {
        return actualLabel;
    }

    public void setActualLabel(int actualLabel) {
        this.actualLabel = actualLabel;
    }

    public int getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(int predictedLabel) {
        this.predictedLabel = predictedLabel;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
