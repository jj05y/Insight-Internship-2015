package classify.ObjectClasses;

/**
 * Created by joe on 02/07/15.
 */
public class FilmSection {

    double startTime;
    double endTime;

    public FilmSection(double startTime, double endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getLength() {
        return endTime - startTime;
    }

    @Override
    public String toString() {
        return ("Start: " + String.format("%.2f", startTime) + "  \tEnd: " + String.format("%.2f", endTime) + "  \tLength" + String.format("%.2f", endTime - startTime));
    }
}
