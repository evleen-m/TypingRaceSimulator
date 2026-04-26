/**
 * stores the saved data for one completed race
 */
public class RaceRecord {
    private String typistName;
    private int raceNumber;
    private int finishPosition;
    private double wpm;
    private double accuracyPercentage;
    private int burnoutCount;
    private double accuracyChange;
    private int pointsEarned;

    /**
     * creates a new race record with all recorded stats
     */
    public RaceRecord(String typistName, int raceNumber, int finishPosition, double wpm, double accuracyPercentage,
                      int burnoutCount, double accuracyChange, int pointsEarned) {
        this.typistName = typistName;
        this.raceNumber = raceNumber;
        this.finishPosition = finishPosition;
        this.wpm = wpm;
        this.accuracyPercentage = accuracyPercentage;
        this.burnoutCount = burnoutCount;
        this.accuracyChange = accuracyChange;
        this.pointsEarned = pointsEarned;
    }


    /**
     * converts this record to a file line format
     */
    public String toFileLine() {
        return typistName + "," +
                raceNumber + "," +
                finishPosition + "," +
                wpm + "," +
                accuracyPercentage + "," +
                burnoutCount + "," +
                accuracyChange + "," +
                pointsEarned;
    }

    /**
     * creates a race record from a line in the file
     */
    public static RaceRecord fromFileLine(String line) {
        String[] parts = line.split(",");

        return new RaceRecord(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4]),
                Integer.parseInt(parts[5]), Double.parseDouble(parts[6]), Integer.parseInt(parts[7]));
    }

    // getter/setter methods
    public String getTypistName() {
        return typistName;
    }

    public int getRaceNumber() {
        return raceNumber;
    }

    public int getFinishPosition() {
        return finishPosition;
    }

    public double getWpm() {
        return wpm;
    }

    public double getAccuracyPercentage() {
        return accuracyPercentage;
    }

    public int getBurnoutCount() {
        return burnoutCount;
    }

    public double getAccuracyChange() {
        return accuracyChange;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }


}