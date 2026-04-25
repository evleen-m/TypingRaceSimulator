import java.util.ArrayList;

/**
 * Stores all race records for one typist.
 */
public class RawData
{
    private String typistName;
    private ArrayList<RaceRecord> raceHistory;
    private double personalBestWpm;

    public RawData(String typistName)
    {
        this.typistName = typistName;
        raceHistory = new ArrayList<>();
        personalBestWpm = 0;
    }

    public String getTypistName()
    {
        return typistName;
    }

    public ArrayList<RaceRecord> getRaceHistory()
    {
        return raceHistory;
    }

    public void addRaceRecord(RaceRecord record)
    {
        raceHistory.add(record);

        if (record.getWpm() > personalBestWpm)
        {
            personalBestWpm = record.getWpm();
        }
    }

    public double getPersonalBestWpm()
    {
        return personalBestWpm;
    }
}