import java.util.ArrayList;

/**
 * Stores race statistics.
 * Keeps current race WPM history and long-term race history.
 */
public class RaceStats
{
    private ArrayList<ArrayList<Double>> wpmHistory;
    private ArrayList<ArrayList<RaceRecord>> raceHistory;
    private double[] personalBestWpm;
    private int raceNumber = 0;

    public RaceStats(int numberOfTypists)
    {
        wpmHistory = new ArrayList<>();
        raceHistory = new ArrayList<>();
        personalBestWpm = new double[numberOfTypists];

        // create one history list for each typist
        for (int i = 0; i < numberOfTypists; i++)
        {
            wpmHistory.add(new ArrayList<>());
            raceHistory.add(new ArrayList<>());
            personalBestWpm[i] = 0;
        }
    }

    /**
     * Records one WPM value for a typist during the current race.
     *
     * @param typistIndex the typist index
     * @param wpm the WPM value
     */
    public void recordWPM(int typistIndex, double wpm)
    {
        if (typistIndex >= 0 && typistIndex < wpmHistory.size())
        {
            wpmHistory.get(typistIndex).add(wpm);
        }
    }

    /**
     * Gets the current race WPM history for one typist.
     *
     * @param typistIndex the typist index
     * @return the WPM history
     */
    public ArrayList<Double> getWpmHistory(int typistIndex)
    {
        if (typistIndex >= 0 && typistIndex < wpmHistory.size())
        {
            return wpmHistory.get(typistIndex);
        }

        return new ArrayList<>();
    }

    /**
     * Finds the longest WPM history.
     *
     * @return the most recorded turns
     */
    public int getLongestHistory()
    {
        int longest = 0;

        for (int i = 0; i < wpmHistory.size(); i++)
        {
            longest = Math.max(longest, wpmHistory.get(i).size());
        }

        return longest;
    }

    /**
     * Saves a completed race into long-term history.
     *
     * @param typists the typists in the race
     * @param race the race object
     */
    public void saveCompletedRace(Typist[] typists, Game race)
    {
        raceNumber++;

        for (int i = 0; i < typists.length; i++)
        {
            if (typists[i] != null)
            {
                double wpm = race.calculateWPM(typists[i]);

                RaceRecord record = new RaceRecord(
                        raceNumber,
                        typists[i].getFinishPosition(),
                        wpm,
                        typists[i].getAccuracyPercentage(),
                        typists[i].getBurnoutCount()
                );

                raceHistory.get(i).add(record);

                if (wpm > personalBestWpm[i])
                {
                    personalBestWpm[i] = wpm;
                }
            }
        }
    }

    /**
     * Gets a typist's personal best WPM.
     *
     * @param typistIndex the typist index
     * @return the personal best WPM
     */
    public double getPersonalBestWpm(int typistIndex)
    {
        if (typistIndex >= 0 && typistIndex < personalBestWpm.length)
        {
            return personalBestWpm[typistIndex];
        }

        return 0;
    }

    /**
     * Gets the full race history for one typist.
     *
     * @param typistIndex the typist index
     * @return the race history
     */
    public ArrayList<RaceRecord> getRaceHistory(int typistIndex)
    {
        if (typistIndex >= 0 && typistIndex < raceHistory.size())
        {
            return raceHistory.get(typistIndex);
        }

        return new ArrayList<>();
    }

    public int getRaceNumber()
    {
        return raceNumber;
    }

    /**
     * Resets only the current race graph data.
     * Long-term history and personal bests are kept.
     */
    public void reset()
    {
        for (int i = 0; i < wpmHistory.size(); i++)
        {
            wpmHistory.get(i).clear();
        }
    }
}