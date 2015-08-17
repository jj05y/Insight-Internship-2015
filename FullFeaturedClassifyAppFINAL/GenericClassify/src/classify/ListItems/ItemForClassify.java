package classify.ListItems;

/**
 * Created by joe on 21/07/15.
 */
public class ItemForClassify {

    int rowID;
    int actualLabel;
    int predictedLabel;
    int rep;
    int exercise;

    String name;
    String featureString;

    public ItemForClassify(){

    }

    public ItemForClassify(int rowID, int actualLabel, int predictedLabel, String name, String featureString, int rep, int exercise) {
        this.rowID = rowID;
        this.actualLabel = actualLabel;
        this.predictedLabel = predictedLabel;
        this.name = name;
        this.featureString = featureString;
        this.rep = rep;
        this.exercise = exercise;
    }

    public int getExercise() {
        return exercise;
    }

    public void setExercise(int exercise) {
        this.exercise = exercise;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeatureString() {
        return featureString;
    }

    public void setFeatureString(String featureString) {
        this.featureString = featureString;
    }
}
