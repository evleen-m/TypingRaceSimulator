import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.util.Random;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * @author Evleen Manghera
 * @version 1
 */
public class Game {

    private int passageLength;
    private String passageText;
    private Typist[] seats;
    private int numberOfSeats;

    private final double MISTYPE_BASE_CHANCE = 0.3;
    private final int BURNOUT_DURATION = 3;

    private int slideBackAmount = 2;
    private double burnoutRisk = 0.05;

    private int turnCount = 0;
    private boolean finished = false;
    private Typist winner = null;

    private boolean autocorrectOn = false;
    private boolean nightShiftOn = false;
    private boolean coffeeModeOn = false;


    // difficulty modifiers
    private final ArrayList<String> names = new ArrayList<>(Arrays.asList(
            "TURBO-FINGERS",
            "QWERTY_QUEEN",
            "HUNT_N_PECK",
            "KEY-SMASHER",
            "SPEED_TYPER",
            "CTRL_MASTER",
            "SHIFT_WARRIOR",
            "BYTE_BLAZER",
            "CODE_TYPHOON",
            "RAPID_KEYS"
    ));


    // empty game object
    public Game() {
    }

    public void StartRace(GameConfig config, TypistConfig[] typistConfigs)
    {
        resetRace();

        numberOfSeats = config.playerNumber;
        setPassageText(config.passageText);

        seats = new Typist[numberOfSeats];
        Arrays.fill(seats, null);


        autocorrectOn = config.autocorrectOn;
        coffeeModeOn = config.coffeeModeOn;
        nightShiftOn = config.nightShiftOn;

        if (autocorrectOn)
        {
            slideBackAmount = slideBackAmount / 2;
        }

        createTypists(typistConfigs);
    }

    private void createTypists(TypistConfig[] typistConfigs)
    {
        for (int i = 0; i < numberOfSeats; i++)
        {
            char symbol = (char) (0x24B6 + i);
            String name = pickTypistName();
            double accuracy = randTypistAccuracy();

            if (nightShiftOn)
            {
                accuracy = accuracy - 0.07;
            }

            Typist typist = new Typist(symbol, name, accuracy);

            if (typistConfigs != null && i < typistConfigs.length)
            {
                typist.setConfig(typistConfigs[i]);
            }
            else
            {
                typist.setConfig(new TypistConfig());
            }

            addTypist(typist, i);
        }
    }

    /**
     * Seats a typist at the given seat number.
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat in which to place them
     */
    public void addTypist(Typist theTypist, int seatNumber) {
        if (seatNumber > numberOfSeats) {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
            return;
        }

        seats[seatNumber] = theTypist;
    }

    /**
     * Resets the race state so that a new race can begin.
     */
    public void resetRace() {
        turnCount = 0;
        finished = false;
        winner = null;
        autocorrectOn = false;
        nightShiftOn = false;
        coffeeModeOn = false;

        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null) {
                seats[i].resetToStart();
                seats[i].setRandColor();
            }
        }

    }

    /**
     * Advances the whole race by one turn.
     */
    public void advanceOneTurn() {
        if (finished) {
            return;
        }

        turnCount++;

        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null) {
                seats[i].setJustMistyped(false);
                advanceTypist(seats[i]);
            }
        }

        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null && raceFinishedBy(seats[i])) {
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
    private void advanceTypist(Typist theTypist) {
        //recover from burnout
        if (theTypist.isBurntOut()) {
            theTypist.recoverFromBurnout();
            return;
        }

        TypistConfig config = theTypist.getConfig();

        double accuracy = theTypist.getAccuracy();

        // only use preset accuracies if config it not null
        if (config != null) {
            accuracy = accuracy + config.getAccuracyBonus(passageLength, theTypist.getProgress());
        }
        //clamp accuracy
        accuracy = Math.max(0.0, Math.min(1.0, accuracy));


        if (Math.random() < (1 - accuracy) * MISTYPE_BASE_CHANCE) {
            theTypist.slideBack(slideBackAmount);
            theTypist.setJustMistyped(true);
        } else if (Math.random() < accuracy) {
            theTypist.typeCharacter();

            // Caffeine Mode: speed boost for the first 10 turns
            if (coffeeModeOn && turnCount <= 10) {
                if (Math.random() < 0.7) {
                    theTypist.typeCharacter();
                }
            }
        }

        double currentBurnoutRisk = burnoutRisk * accuracy * accuracy;

        if (config != null) {
            currentBurnoutRisk = currentBurnoutRisk * config.getBurnoutMultiplier();
        }

        // Caffeine Mode: after 10 turns, burnout risk increases exponentially
        if (coffeeModeOn && turnCount > 10) {
            currentBurnoutRisk = currentBurnoutRisk * 3;
        }

        if (Math.random() < currentBurnoutRisk) {
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.04);
            theTypist.burnOut(BURNOUT_DURATION);
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist) {
        return (theTypist.getProgress() >= passageLength);
    }


    /**
     * Calculates the current WPM for a typist.
     *
     * @param t the typist
     * @return the WPM estimate
     */
    public double calculateWPM(Typist t) {
        if (turnCount == 0) {
            return 0;
        }

        double minutes = (turnCount * 0.2) / 60.0;
        double words = t.getProgress() / 5.0;

        return words / minutes;
    }

    /**
     * Sets the passage text used in the race.
     *
     * @param text the new passage text
     */
    public void setPassageText(String text) {
        if (text == null || text.trim().isEmpty()) {
            passageText = "The quick brown fox jumps over the lazy dog.";
        } else {
            passageText = text.trim();
        }

        passageLength = passageText.length();
    }

    public int getPassageLength() {
        return passageLength;
    }

    public String getPassageText() {
        return passageText;
    }

    public Typist[] getSeats() {
        return seats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public boolean isFinished() {
        return finished;
    }

    public Typist getWinner() {
        return winner;
    }

    private String pickTypistName() {
        Random rand = new Random();
        if (names.isEmpty()) {
            return "NO_NAMES_LEFT";
        }
        int index = rand.nextInt(names.size());
        return names.remove(index); // removes so it can't repeat
    }

    private double randTypistAccuracy() {
        double num = Math.random();
        // create variation -> ensures some weaker and stronger but most are mid-capability
        if (num < 0.2) {
            num = 0.50 + Math.random() * 0.08; // weaker
        } else if (num < 0.8) {
            num = 0.58 + Math.random() * 0.17; // average
        } else {
            num = 0.75 + Math.random() * 0.10; // stronger
        }
        return num;
    }

    public String getModifiers()
    {
        String msg = "";
        if (autocorrectOn){
            msg = msg + "\n Autocorrect";
        }
        if (coffeeModeOn){
            msg = msg + "\n Coffee Mode";
        }
        if (nightShiftOn){
            msg = msg + "\n Night Shift";
        }

        if (msg.isEmpty()) {
            return " N/A";
        }
        return msg;
    }
}