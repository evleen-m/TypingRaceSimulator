import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import javax.swing.SwingUtilities;
import java.util.Random;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * @author Evleen Manghera
 * @version 1
 */
public class TypingRace
{
    private int passageLength;
    private String passageText;
    private final Typist[] seats;
    private final int numberOfSeats;

    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int SLIDE_BACK_AMOUNT = 3;
    private static final int BURNOUT_DURATION = 3;

    private int turnCount = 0;
    private boolean finished = false;
    private Typist winner = null;

    private static final ArrayList<String> names = new ArrayList<>(Arrays.asList(
            "TURBOFINGERS",
            "QWERTY_QUEEN",
            "HUNT_N_PECK",
            "KEYSMASHER",
            "SPEED_TYPER",
            "CTRL_MASTER",
            "SHIFT_WARRIOR",
            "BYTE_BLAZER",
            "CODE_TYPHOON",
            "RAPID_KEYS"
    ));



    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given text.
     *
     * @param passageText the passage text to type
     * @param seatsLength the number of seats in the race
     */
    public TypingRace(String passageText, int seatsLength)
    {
        setPassageText(passageText);

        seats = new Typist[seatsLength + 1];
        Arrays.fill(seats, null);
        this.numberOfSeats = seatsLength + 1;

        createTypists(this, numberOfSeats);
    }

    /**
     * Seats a typist at the given seat number.
     *
     * @param theTypist the typist to seat
     * @param seatNumber the seat in which to place them
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber == 0)
        {
            System.out.println("cannot seat typist at seat 0");
            return;
        }

        if (seatNumber >= numberOfSeats)
        {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
            return;
        }

        seats[seatNumber] = theTypist;
    }

    /**
     * Starts the typing race in terminal mode.
     */
    public void startRace()
    {
        resetRace();

        while (!finished)
        {
            advanceOneTurn();
            printRace();

            try
            {
                TimeUnit.MILLISECONDS.sleep(200);
            }
            catch (Exception e) {}
        }

        if (winner != null)
        {
            System.out.println();
            System.out.println("And the winner is... " + winner.getName() + "!");
            System.out.println("Final accuracy: " + String.format("%.2f", winner.getAccuracy()));
        }
    }

    /**
     * Starts the GUI version of the race.
     */
    public void startRaceGUI()
    {
        resetRace();

        SwingUtilities.invokeLater(() -> {
            TypingRaceFrame frame = new TypingRaceFrame(this);
            frame.show();
        });
    }

    /**
     * Resets the race state so that a new race can begin.
     */
    public void resetRace()
    {
        turnCount = 0;
        finished = false;
        winner = null;

        for (int i = 1; i < numberOfSeats; i++)
        {
            if (seats[i] != null)
            {
                seats[i].resetToStart();
            }
        }
    }

    /**
     * Advances the whole race by one turn.
     */
    public void advanceOneTurn()
    {
        if (finished)
        {
            return;
        }

        turnCount++;

        for (int i = 1; i < numberOfSeats; i++)
        {
            if (seats[i] != null)
            {
                seats[i].setJustMistyped(false);
                advanceTypist(seats[i]);
            }
        }

        for (int i = 1; i < numberOfSeats; i++)
        {
            if (seats[i] != null && raceFinishedBy(seats[i]))
            {
                winner = seats[i];
                finished = true;
                winner.setAccuracy(winner.getAccuracy() + 0.02);
                break;
            }
        }
    }

    /**
     * Simulates one turn for a typist.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(Typist theTypist)
    {
        if (theTypist.isBurntOut())
        {
            theTypist.recoverFromBurnout();
            return;
        }

        if (Math.random() < (1 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            theTypist.setJustMistyped(true);
        }
        else if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.02);
            theTypist.burnOut(BURNOUT_DURATION);
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist)
    {
        return (theTypist.getProgress() >= passageLength);
    }

    /**
     * Prints the current state of the race to the terminal.
     */
    private void printRace()
    {
        System.out.print('\u000C');

        System.out.println("  TYPING RACE — passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        for (int i = 1; i < numberOfSeats; i++)
        {
            if (seats[i] != null)
            {
                printSeat(seats[i]);
            }
            System.out.println();
        }

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     *
     * @param theTypist the typist whose lane to print
     */
    private void printSeat(Typist theTypist)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter = passageLength - theTypist.getProgress();

        if (spacesAfter < 0)
        {
            spacesAfter = 0;
        }

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        System.out.print(theTypist.getSymbol());

        if (theTypist.hasJustMistyped())
        {
            System.out.print('<');
            spacesAfter--;
        }

        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--;
        }

        if (spacesAfter < 0)
        {
            spacesAfter = 0;
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                    + " (Accuracy: " + String.format("%.2f", theTypist.getAccuracy()) + ")"
                    + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else
        {
            System.out.print(theTypist.getName() + " (Accuracy: "
                    + String.format("%.2f", theTypist.getAccuracy())
                    + ", WPM: " + String.format("%.2f", calculateWPM(theTypist)) + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        for (int i = 0; i < times; i++)
        {
            System.out.print(aChar);
        }
    }

    /**
     * Calculates the current WPM for a typist.
     *
     * @param t the typist
     * @return the WPM estimate
     */
    public double calculateWPM(Typist t)
    {
        if (turnCount == 0)
        {
            return 0;
        }

        double minutes = (turnCount * 0.2) / 60.0;
        double words = t.getProgress() / 5.0;

        return words / minutes;
    }

    /**
     * Sets the passage text used in the race.
     *
     * @param passageText the new passage text
     */
    public void setPassageText(String passageText)
    {
        if (passageText == null || passageText.trim().isEmpty())
        {
            this.passageText = "The quick brown fox jumps over the lazy dog.";
        }
        else
        {
            this.passageText = passageText.trim();
        }

        this.passageLength = this.passageText.length();
    }

    public int getPassageLength()
    {
        return passageLength;
    }

    public String getPassageText()
    {
        return passageText;
    }

    public Typist[] getSeats()
    {
        return seats;
    }

    public int getNumberOfSeats()
    {
        return numberOfSeats;
    }

    public int getTurnCount()
    {
        return turnCount;
    }

    public boolean isFinished()
    {
        return finished;
    }

    public Typist getWinner()
    {
        return winner;
    }

    private void createTypists(TypingRace race,int numberOfSeats){
        for (int i = 1; i < numberOfSeats; i++) {
            char symbol = (char) (0x24B5 + i);
            String name = pickTypistName();
            double accuracy = randTypistAccuracy();
            race.addTypist(new Typist(symbol,name,accuracy), i);
        }
    }

    private String pickTypistName(){
        Random rand = new Random();
        if (names.isEmpty())
        {
            return "NO_NAMES_LEFT";
        }
        int index = rand.nextInt(names.size());
        return names.remove(index); // removes so it can't repeat
    }

    private double randTypistAccuracy(){
        double num = Math.random();
        // create variation -> ensures some weaker and stronger but most are mid capability
        if (num < 0.2)
        {
            num = 0.50 + Math.random() * 0.08; // weaker
        }
        else if (num < 0.8)
        {
            num = 0.58 + Math.random() * 0.17; // average
        }
        else
        {
            num = 0.75 + Math.random() * 0.10; // stronger
        }
        return num;
    }


    public static void main(String[] args)
    {
        TypingRace race = new TypingRace("HI", 5);
        race.startRaceGUI();
    }
}