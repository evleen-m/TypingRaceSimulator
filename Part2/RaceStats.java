import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * calculates race statistics and stores or reads historical typist data
 */
public class RaceStats {
    private static final String FILE_PATH = System.getProperty("user.home") + "/temp/cs/typing-race-history.txt";

    private HashMap<String, RawData> typistData;
    private HashMap<String, ArrayList<Double>> wpmHistory;
    private int raceNumber;

    public RaceStats() {
        typistData = new HashMap<>();
        wpmHistory = new HashMap<>();
        raceNumber = 0;

        checkForDataFile();
        loadHistoryFromFile();
    }

    public void reset() {
        wpmHistory.clear();
    }

    /**
     * records a wpm value for a typist during the race
     * creates a new history list if the typist is not already stored
     *
     * @param typistName the typist name
     * @param wpm        the wpm value to record
     */
    public void recordWPM(String typistName, double wpm) {
        if (!wpmHistory.containsKey(typistName)) {
            wpmHistory.put(typistName, new ArrayList<>());
        }

        wpmHistory.get(typistName).add(wpm);
    }

    /**
     * saves all typist results from a completed race
     * creates records calculates points and updates stored data
     *
     * @param typists array of typists in the race
     * @param game    the game instance used to calculate wpm
     */
    public void saveCompletedRace(Typist[] typists, TypingRace game) {
        raceNumber++;

        for (Typist typist : typists) {
            if (typist != null) {
                double wpm = game.calculateWPM(typist);
                int points = calculatePoints(typist, wpm);

                RaceRecord record = new RaceRecord(
                        typist.getName(),
                        raceNumber,
                        typist.getFinishPosition(),
                        wpm,
                        typist.getAccuracyPercentage(),
                        typist.getBurnoutCount(),
                        typist.getAccuracyChange(),
                        points
                );

                saveRecordToFile(record);
                addRecordToRawData(record);
            }
        }
    }

    /**
     * calculates points earned for a typist in a race
     * based on finish position wpm and burnouts
     *
     * @param typist the typist
     * @param wpm    the typists wpm
     * @return total points earned
     */
    public int calculatePoints(Typist typist, double wpm) {
        int points = 0;

        // calculate based on pos
        if (typist.getFinishPosition() == 1) {
            points += 3;
        } else if (typist.getFinishPosition() == 2) {
            points += 2;
        } else if (typist.getFinishPosition() == 3) {
            points += 1;
        }

        // calculate based on wpm
        points += (int) (wpm / 20);

        // calculate based on burnouts
        if (typist.getBurnoutCount() == 0) {
            points += 1;
        } else {
            points -= typist.getBurnoutCount();
        }

        // make sure points are never less than 0
        if (points < 0) {
            points = 0;
        }

        return points;
    }

    /**
     * returns the wpm history for a typist
     * returns empty list if no data exists
     *
     * @param typistName the typist name
     * @return list of wpm values
     */
    public ArrayList<Double> getWpmHistory(String typistName) {
        if (!wpmHistory.containsKey(typistName)) {
            return new ArrayList<>();
        }

        return wpmHistory.get(typistName);
    }

    /**
     * finds the longest wpm history among all typists
     *
     * @return size of the longest history list
     */
    public int getLongestHistory() {
        int longest = 0;

        for (ArrayList<Double> history : wpmHistory.values()) {
            if (history.size() > longest) {
                longest = history.size();
            }
        }

        return longest;
    }

    /**
     * returns the race history for a specific typist
     * returns empty list if no data exists
     *
     * @param typistName the typist name
     * @return list of race records
     */
    public ArrayList<RaceRecord> getRaceHistory(String typistName) {
        if (!typistData.containsKey(typistName)) {
            return new ArrayList<>();
        }

        return typistData.get(typistName).getRaceHistory();
    }

    /**
     * returns the personal best wpm for a typist
     * returns 0 if no data exists
     *
     * @param typistName the typist name
     * @return highest recorded wpm
     */
    public double getPersonalBestWpm(String typistName) {
        if (!typistData.containsKey(typistName)) {
            return 0;
        }

        return typistData.get(typistName).getPersonalBestWpm();
    }

    /**
     * returns total points for a typist
     * returns 0 if no data exists
     *
     * @param typistName the typist name
     * @return cumulative points
     */
    public int getCumulativePoints(String typistName) {
        if (!typistData.containsKey(typistName)) {
            return 0;
        }

        return typistData.get(typistName).getCumulativePoints();
    }

    /**
     * returns leaderboard sorted by cumulative points
     *
     * @return sorted list of raw data
     */
    public ArrayList<RawData> getLeaderboard() {
        ArrayList<RawData> leaderboard = new ArrayList<>(typistData.values());

        insertionSortLeaderboard(leaderboard);

        return leaderboard;
    }

    /**
     * checks if the data file exists and creates it if not
     */
    private static void checkForDataFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * loads race history data from the file
     * rebuilds typist data and updates the latest race number
     */
    private void loadHistoryFromFile() {
        typistData.clear();
        raceNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                RaceRecord record = RaceRecord.fromFileLine(line);
                addRecordToRawData(record);

                if (record.getRaceNumber() > raceNumber) {
                    raceNumber = record.getRaceNumber();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * adds a race record to the correct typists raw data
     * creates a new entry if the typist does not exist yet
     *
     * @param record the race record to add
     */
    private void addRecordToRawData(RaceRecord record) {
        String typistName = record.getTypistName();

        if (!typistData.containsKey(typistName)) {
            typistData.put(typistName, new RawData(typistName));
        }

        typistData.get(typistName).addRaceRecord(record);
    }

    /**
     * saves a single race record to the data file
     * appends the record so existing data is kept
     *
     * @param record the race record to save
     */
    private void saveRecordToFile(RaceRecord record) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(record.toFileLine() + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sorts the leaderboard using insertion sort by cumulative points descending
     *
     * @param list the leaderboard list
     */
    private void insertionSortLeaderboard(ArrayList<RawData> list) {
        for (int i = 1; i < list.size(); i++) {
            RawData key = list.get(i);
            int j = i - 1;

            while (j >= 0 && list.get(j).getCumulativePoints() < key.getCumulativePoints()) {
                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }
    }
}