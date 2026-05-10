package entities;


public class ThresholdRange {
    private double minThreshold;
    private double maxThreshold;

    public ThresholdRange(double minThreshold, double maxThreshold) {
        if (minThreshold > maxThreshold) {
            throw new IllegalArgumentException("minThreshold cannot be greater than maxThreshold");
        }
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    public double getMinThreshold() { return minThreshold; }
    public double getMaxThreshold() { return maxThreshold; }

    public void setMinThreshold(double minThreshold) {
        if (minThreshold > this.maxThreshold) {
            throw new IllegalArgumentException("minThreshold cannot be greater than maxThreshold");
        }
        this.minThreshold = minThreshold;
    }

    public void setMaxThreshold(double maxThreshold) {
        if (this.minThreshold > maxThreshold) {
            throw new IllegalArgumentException("maxThreshold cannot be smaller than minThreshold");
        }
        this.maxThreshold = maxThreshold;
    }

    public boolean isOutOfRange(double value) {
        return value < minThreshold || value > maxThreshold;
    }
}