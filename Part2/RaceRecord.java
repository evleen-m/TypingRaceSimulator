/**
 * Stores one completed race result for one typist.
 */
public class RaceRecord
{
    private int raceNumber;
    private int finishPosition;
    private double wpm;
    private double accuracyPercentage;
    private int burnoutCount;

    public RaceRecord(int raceNumber, int finishPosition, double wpm, double accuracyPercentage, int burnoutCount)
    {
        this.raceNumber = raceNumber;
        this.finishPosition = finishPosition;
        this.wpm = wpm;
        this.accuracyPercentage = accuracyPercentage;
        this.burnoutCount = burnoutCount;
    }

    public int getRaceNumber()
    {
        return raceNumber;
    }

    public int getFinishPosition()
    {
        return finishPosition;
    }

    public double getWpm()
    {
        return wpm;
    }

    public double getAccuracyPercentage()
    {
        return accuracyPercentage;
    }

    public int getBurnoutCount()
    {
        return burnoutCount;
    }
}