import java.util.ArrayList;

/**
 * stores all race records for one typist.
 */
public class RawData {
    private String typistName;
    private ArrayList<RaceRecord> raceHistory;
    private double personalBestWpm;
    private int cumulativePoints;

    public RawData(String typistName) {
        this.typistName = typistName;
        raceHistory = new ArrayList<>();
        personalBestWpm = 0;
        cumulativePoints = 0;
    }

    /**
     * adds a race record to this typists history
     * updates total points and personal best wpm
     *
     * @param record the race record to add
     */
    public void addRaceRecord(RaceRecord record) {
        raceHistory.add(record);
        cumulativePoints += record.getPointsEarned();

        if (record.getWpm() > personalBestWpm) {
            personalBestWpm = record.getWpm();
        }
    }

    /**
     * counts how many wins in a row this typist has
     * checks from most recent race backwards until a loss
     *
     * @return number of consecutive wins
     */
    public int getConsecutiveWins() {
        int wins = 0;

        for (int i = raceHistory.size() - 1; i >= 0; i--) {
            if (raceHistory.get(i).getFinishPosition() == 1) {
                wins++;
            } else {
                break;
            }
        }

        return wins;
    }

    /**
     * counts how many races were completed without any burnout
     *
     * @return number of no burnout races
     */
    public int getNoBurnoutRaceCount() {
        int count = 0;

        for (RaceRecord record : raceHistory) {
            if (record.getBurnoutCount() == 0) {
                count++;
            }
        }

        return count;
    }


    public String getTitle() {
        StringBuilder titles = new StringBuilder();

        if (getConsecutiveWins() >= 3) {
            titles.append("Speed Demon | ");
        }

        if (getNoBurnoutRaceCount() >= 5) {
            titles.append("Iron Fingers | ");
        }

        if (personalBestWpm >= 70) {
            titles.append("Keyboard Legend | ");
        }

        if (cumulativePoints >= 25) {
            titles.append("Race Champion | ");
        }

        // default if none earned
        if (titles.isEmpty()) {
            return "Rookie Racer";
        }

        return titles.substring(0, titles.length() - 3);
    }

    // getter/seter methods

    public String getTypistName() {
        return typistName;
    }

    public ArrayList<RaceRecord> getRaceHistory() {
        return raceHistory;
    }

    public double getPersonalBestWpm() {
        return personalBestWpm;
    }

    public int getCumulativePoints() {
        return cumulativePoints;
    }

}