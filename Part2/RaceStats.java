import java.util.ArrayList;

/**
 * Stores race statistics.
 * For now, only WPM history is stored.
 */
public class RaceStats
{
    private ArrayList<ArrayList<Double>> wpmHistory;

    public RaceStats(int numberOfTypists)
    {
        wpmHistory = new ArrayList<>();

        for (int i = 0; i < numberOfTypists; i++)
        {
            wpmHistory.add(new ArrayList<>());
        }
    }

    public void recordWPM(int typistIndex, double wpm)
    {
        if (typistIndex >= 0 && typistIndex < wpmHistory.size())
        {
            wpmHistory.get(typistIndex).add(wpm);
        }
    }

    public ArrayList<Double> getWpmHistory(int typistIndex)
    {
        if (typistIndex >= 0 && typistIndex < wpmHistory.size())
        {
            return wpmHistory.get(typistIndex);
        }

        return new ArrayList<>();
    }

    public int getLongestHistory()
    {
        int longest = 0;

        for (int i = 0; i < wpmHistory.size(); i++)
        {
            longest = Math.max(longest, wpmHistory.get(i).size());
        }

        return longest;
    }

    public void reset()
    {
        for (int i = 0; i < wpmHistory.size(); i++)
        {
            wpmHistory.get(i).clear();
        }
    }
}