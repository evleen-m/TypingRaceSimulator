import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Calculates race statistics and stores/reads historical typist data.
 */
public class RaceStats
{
    private static final String FILE_PATH = System.getProperty("user.home") + "/temp/cs/typing-race-history.txt";
    private HashMap<String, RawData> typistData;
    private HashMap<String, ArrayList<Double>> wpmHistory;
    private int raceNumber;

    public RaceStats()
    {
        typistData = new HashMap<>();
        wpmHistory = new HashMap<>();
        raceNumber = 0;

        checkForDataFile();
        loadHistoryFromFile();
    }

    public void reset()
    {
        wpmHistory.clear();
    }

    private void checkForDataFile()
    {
        try
        {
            File file = new File(FILE_PATH);

            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
            {
                parent.mkdirs();
            }

            if (!file.exists())
            {
                file.createNewFile();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void loadHistoryFromFile()
    {
        typistData.clear();
        raceNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH)))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                if (line.trim().isEmpty())
                {
                    continue;
                }

                RaceRecord record = RaceRecord.fromFileLine(line);
                addRecordToRawData(record);

                if (record.getRaceNumber() > raceNumber)
                {
                    raceNumber = record.getRaceNumber();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void addRecordToRawData(RaceRecord record)
    {
        String typistName = record.getTypistName();

        if (!typistData.containsKey(typistName))
        {
            typistData.put(typistName, new RawData(typistName));
        }

        typistData.get(typistName).addRaceRecord(record);
    }

    public void recordWPM(String typistName, double wpm)
    {
        if (!wpmHistory.containsKey(typistName))
        {
            wpmHistory.put(typistName, new ArrayList<>());
        }

        wpmHistory.get(typistName).add(wpm);
    }

    public ArrayList<Double> getWpmHistory(String typistName)
    {
        if (!wpmHistory.containsKey(typistName))
        {
            return new ArrayList<>();
        }

        return wpmHistory.get(typistName);
    }

    public int getLongestHistory()
    {
        int longest = 0;

        for (ArrayList<Double> history : wpmHistory.values())
        {
            if (history.size() > longest)
            {
                longest = history.size();
            }
        }

        return longest;
    }

    public void saveCompletedRace(Typist[] typists, Game game)
    {
        raceNumber++;

        for (Typist typist : typists)
        {
            if (typist != null)
            {
                RaceRecord record = new RaceRecord(
                        typist.getName(),
                        raceNumber,
                        typist.getFinishPosition(),
                        game.calculateWPM(typist),
                        typist.getAccuracyPercentage(),
                        typist.getBurnoutCount(),
                        typist.getAccuracyChange()
                );

                saveRecordToFile(record);
                addRecordToRawData(record);
            }
        }
    }

    private void saveRecordToFile(RaceRecord record)
    {
        try (FileWriter writer = new FileWriter(FILE_PATH, true))
        {
            writer.write(record.toFileLine() + System.lineSeparator());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<RaceRecord> getRaceHistory(String typistName)
    {
        if (!typistData.containsKey(typistName))
        {
            return new ArrayList<>();
        }

        return typistData.get(typistName).getRaceHistory();
    }

    public double getPersonalBestWpm(String typistName)
    {
        if (!typistData.containsKey(typistName))
        {
            return 0;
        }

        return typistData.get(typistName).getPersonalBestWpm();
    }

    public RawData getRawData(String typistName)
    {
        return typistData.get(typistName);
    }
}