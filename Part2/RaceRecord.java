/**
 * Stores the saved data for one completed race.
 */
public class RaceRecord
{
    private String typistName;
    private int raceNumber;
    private int finishPosition;
    private double wpm;
    private double accuracyPercentage;
    private int burnoutCount;
    private double accuracyChange;

    public RaceRecord(
            String typistName,
            int raceNumber,
            int finishPosition,
            double wpm,
            double accuracyPercentage,
            int burnoutCount,
            double accuracyChange)
    {
        this.typistName = typistName;
        this.raceNumber = raceNumber;
        this.finishPosition = finishPosition;
        this.wpm = wpm;
        this.accuracyPercentage = accuracyPercentage;
        this.burnoutCount = burnoutCount;
        this.accuracyChange = accuracyChange;
    }

    public String getTypistName()
    {
        return typistName;
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

    public double getAccuracyChange()
    {
        return accuracyChange;
    }

    public String toFileLine()
    {
        return typistName + "," +
                raceNumber + "," +
                finishPosition + "," +
                wpm + "," +
                accuracyPercentage + "," +
                burnoutCount + "," +
                accuracyChange;
    }

    public static RaceRecord fromFileLine(String line)
    {
        String[] parts = line.split(",");

        return new RaceRecord(
                parts[0],
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Double.parseDouble(parts[3]),
                Double.parseDouble(parts[4]),
                Integer.parseInt(parts[5]),
                Double.parseDouble(parts[6])
        );
    }
}