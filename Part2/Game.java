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

    // difficulty modifiers
    private boolean autocorrectOn = false;
    private boolean nightShiftOn = false;
    private boolean coffeeModeOn = false;






    // empty game object
    public Game() {
    }

    public void StartRace(GameConfig config, TypistConfig[] typistConfigs)
    {
        resetRace(typistConfigs);

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
            String chosenSymbol = typistConfigs[i].getSymbol();
            char symbol = chosenSymbol.charAt(0);

            Typist typist = new Typist(symbol);

            if (nightShiftOn)
            {
                typist.setAccuracy(typist.getAccuracy() - 0.07);
            }

            typist.setConfig(typistConfigs[i]);
            typist.setColor(typistConfigs[i].getColour());

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
    public void resetRace(TypistConfig[] typistConfigs) {
        turnCount = 0;
        finished = false;
        winner = null;
        autocorrectOn = false;
        nightShiftOn = false;
        coffeeModeOn = false;

        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null) {
                seats[i].resetToStart();

                if (typistConfigs != null && i < typistConfigs.length) {
                    seats[i].setColor(typistConfigs[i].getColour());
                }
            }
        }

    }
    /**
     * Advances the whole race by one turn.
     */
    // advances the race by one turn for all typists
    public void advanceOneTurn() {
        if (finished) {
            return;
        }

        turnCount++;

        // only move typists who have not finished yet
        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null && !seats[i].isFinished()) {
                seats[i].setJustMistyped(false);
                advanceTypist(seats[i]);
            }
        }

        // check which typists finished this turn
        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null && !seats[i].isFinished() && raceFinishedBy(seats[i])) {

                if (winner == null) {
                    winner = seats[i];
                    winner.setAccuracy(winner.getAccuracy() + 0.02);
                }

                seats[i].setFinished(true);
            }
        }

        // end the whole race only when everyone is finished
        finished = checkAllFinished();
    }
    private boolean checkAllFinished() {
        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null && !seats[i].isFinished()) {
                return false;
            }
        }

        return true;
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

        double mistypeChance = (1 - accuracy) * MISTYPE_BASE_CHANCE;

        if (config != null)
        {
            mistypeChance = mistypeChance * config.getMistypeMultiplier();
        }

        if (Math.random() < mistypeChance) {
            theTypist.slideBack(slideBackAmount);
            theTypist.setJustMistyped(true);
        }

        else if (Math.random() < accuracy)
        {
            // set up speed for use in typing
            double speedMultiplier = 1.0;
            if (config != null)
            {
                speedMultiplier = config.getSpeedMultiplier();
            }

            typeWithSpeed(theTypist, speedMultiplier);

            // Caffeine Mode: speed boost for the first 10 turns
            if (coffeeModeOn && turnCount <= 10) {
                if (Math.random() < 0.7) {
                    typeWithSpeed(theTypist, speedMultiplier);
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

            // check if writst support -> decrease burnout turns by one if there is
            int duration = BURNOUT_DURATION;
            if (config != null)
            {
                duration += config.getBurnoutDurationModifier();
            }
            duration = Math.max(1, duration);
            theTypist.burnOut(duration);
        }
    }

    private void typeWithSpeed(Typist theTypist, double speedMultiplier)
    {
        int guaranteedCharacters = (int) speedMultiplier;
        double extraCharacterChance = speedMultiplier - guaranteedCharacters;

        for (int i = 0; i < guaranteedCharacters; i++)
        {
            theTypist.typeCharacter();
        }

        if (Math.random() < extraCharacterChance)
        {
            theTypist.typeCharacter();
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