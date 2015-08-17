package classify.WorkerClasses;

import org.apache.commons.math.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Skewness;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.rank.Median;

import java.util.ArrayList;
import java.util.Collections;


public class FeatureFinder {
    private double mean;
    private double rMS;
    private double standardDeviation;
    private double kurtosis;
    private double median;
    private double mode;
    private double skewness;
    private double minimum;
    private double range;
    private double variance;
    private double maximum;

    private Mean meaner;
    private StandardDeviation standardDeviationer;
    private Kurtosis kurtosiser;
    private Median medianer; //NOTE: I'm not sure if this one works the same as the others, might have to write my own function
    private Skewness skewnesser;
    private Variance variancer;




    public FeatureFinder(ArrayList<Double> data)
    {
        //put the data in a normal array so the evaluate functions can work with them
        double[] dataArray = new double[data.size()];
        for (int k = 0; k < data.size(); k++)
        {
            dataArray[k] = data.get(k);
        }

        meaner = new Mean();
        mean = meaner.evaluate(dataArray);

        standardDeviationer = new StandardDeviation();
        standardDeviation = standardDeviationer.evaluate(dataArray);

        kurtosiser = new Kurtosis();
        kurtosis = kurtosiser.evaluate(dataArray);

        medianer = new Median();
        median = medianer.evaluate(dataArray);

        skewnesser = new Skewness();
        skewness = skewnesser.evaluate(dataArray);

        variancer = new Variance();
        variance = variancer.evaluate(dataArray);

        rMS = calcRMS(data);
        mode = calcMode(data);
        minimum = Collections.min(data);
        maximum = Collections.max(data);
        range = maximum - minimum;

    }

    private double calcRMS(ArrayList<Double> data) {
        double sum = 0.0;
        int size = 0;
        if(!data.isEmpty())
        {
            for (double d : data)
            {
                sum += d*d;
                size++;
            }
            return Math.sqrt(sum/(double) size);
        }
        return sum;
    }



    private double calcMode(ArrayList<Double> data) {
        double maxValue = 0.0;
        int maxCount = 0;

        for (int i = 0; i < data.size()-1; ++i) {
            int count = 0;
            for (int j = 0; j < data.size(); ++j) {
                if (data.get(j) == data.get(i)) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = data.get(i);
            }
        }

        return maxValue;
    }

    @Override
    public String toString() {
        return mean +
                "," + rMS +
                "," + standardDeviation +
                "," + kurtosis +
                "," + median +
                "," + mode +
                "," + skewness +
                "," + minimum +
                "," + range +
                "," + variance +
                "," + maximum;

    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getrMS() {
        return rMS;
    }

    public void setrMS(double rMS) {
        this.rMS = rMS;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public void setKurtosis(double kurtosis) {
        this.kurtosis = kurtosis;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getMode() {
        return mode;
    }

    public void setMode(double mode) {
        this.mode = mode;
    }

    public double getSkewness() {
        return skewness;
    }

    public void setSkewness(double skewness) {
        this.skewness = skewness;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }
}


