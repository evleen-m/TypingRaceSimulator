import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a typist in the typing race.
 *
 * @author Evleen Manghera
 * @version 1
 */
public class Typist {

    private String typistName;
    private char typistSymbol;
    private int progress;
    private boolean burntOut;
    private int burntOutTurnsRemaining;
    private double accuracyRating;
    private boolean justMistyped;
    private Color color;
    private boolean finished = false;

    private final double INITAL_ACCURACY;


    // configuration
    private TypistConfig config;

    // stats
    private int correctKeystrokes = 0;
    private int mistypes = 0;
    private int burnoutCount = 0;
    private double startingAccuracy;
    private int finishTurn = 0;
    private int finishPosition = 0;
    private ArrayList<Integer> burnoutTurns = new ArrayList<>();

    /**
     * creates a new typist with a name and symbol
     * sets starting values like progress and accuracy
     *
     * @param name   the typist name
     * @param symbol the character used to represent the typist
     */
    public Typist(String name, char symbol) {
        typistName = name;
        typistSymbol = symbol;
        progress = 0;
        burntOut = false;
        burntOutTurnsRemaining = 0;
        accuracyRating = randTypistAccuracy();
        INITAL_ACCURACY = accuracyRating;
        justMistyped = false;
        startingAccuracy = accuracyRating;
    }

    // main race actions

    /**
     * resets the typist back to starting state for a new race
     * clears progress status and all stats
     */
    public void resetToStart() {
        progress = 0;
        burntOut = false;
        burntOutTurnsRemaining = 0;
        justMistyped = false;
        finished = false;

        // reset stats vars
        correctKeystrokes = 0;
        mistypes = 0;
        burnoutCount = 0;
        finishTurn = 0;
        finishPosition = 0;
        startingAccuracy = accuracyRating;
        burnoutTurns.clear();
    }

    /**
     * moves the typist forward by one character in the race
     */
    public void typeCharacter() {
        progress += 1;
    }

    /**
     * moves the typist backwards by a set amount
     * makes sure progress does not go below zero
     *
     * @param amount how many characters to move back
     */
    public void slideBack(int amount) {
        if (progress >= amount) {
            progress -= amount;
        } else {
            progress = 0;
        }
    }

    /**
     * sets the typist into a burnout state for a number of turns
     * prevents progress while burnout is active
     *
     * @param turns how long the burnout lasts
     */
    public void burnOut(int turns) {
        this.burntOut = true;
        this.burntOutTurnsRemaining = turns;
    }

    /**
     * reduces burnout duration each turn
     * typist recovers when turns reach zero
     */
    public void recoverFromBurnout() {
        if (burntOut) {
            burntOutTurnsRemaining--;
            if (burntOutTurnsRemaining == 0) {
                burntOut = false;
            }
        }
    }

    // stat methods

    /**
     * records a burnout event for this typist
     * increases burnout count and stores the turn it happened
     *
     * @param turnNumber the turn when burnout occurred
     */
    public void recordBurnout(int turnNumber) {
        burnoutCount++;
        burnoutTurns.add(turnNumber);
    }

    public void recordCorrectKeystroke() {
        correctKeystrokes++;
    }

    public void recordMistype() {
        mistypes++;
    }

    // calculation methods

    /**
     * calculates how much accuracy has changed since the start of the race
     *
     * @return difference between current and starting accuracy
     */
    public double getAccuracyChange() {
        return getAccuracy() - startingAccuracy;
    }

    /**
     * calculates the accuracy percentage based on correct keystrokes and mistakes
     *
     * @return accuracy as a percentage
     */
    public double getAccuracyPercentage() {
        int total = correctKeystrokes + mistypes;

        if (total == 0) {
            return 0;
        }

        return ((double) correctKeystrokes / total) * 100;
    }

    /**
     * generates a random starting accuracy for the typist
     * creates variation so most are average with some weaker and stronger
     *
     * @return random accuracy value
     */
    private double randTypistAccuracy() {
        double num = Math.random();

        if (num < 0.2) {
            num = 0.50 + Math.random() * 0.08; // weaker
        } else if (num < 0.8) {
            num = 0.58 + Math.random() * 0.17; // average
        } else {
            num = 0.75 + Math.random() * 0.10; // stronger
        }

        return num;
    }

    // getters/setters

    public double getINITAL_ACCURACY() {
        return INITAL_ACCURACY;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setJustMistyped(boolean justMistyped) {
        this.justMistyped = justMistyped;
    }

    public boolean hasJustMistyped() {
        return justMistyped;
    }

    public double getAccuracy() {
        return accuracyRating;
    }

    public int getProgress() {
        return progress;
    }

    public String getName() {
        return typistName;
    }

    public char getSymbol() {
        return typistSymbol;
    }

    public int getBurnoutTurnsRemaining() {
        return burntOutTurnsRemaining;
    }


    public boolean isBurntOut() {
        return burntOut;
    }

    /**
     * sets the accuracy rating for the typist
     * keeps the value between 0 and 1
     *
     * @param newAccuracy the new accuracy value
     */
    public void setAccuracy(double newAccuracy) {
        if (newAccuracy < 0.0) {
            accuracyRating = 0.0;
        } else if (newAccuracy > 1.0) {
            accuracyRating = 1.0;
        } else {
            accuracyRating = newAccuracy;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public TypistConfig getConfig() {
        return config;
    }

    public void setConfig(TypistConfig config) {
        this.config = config;
    }

    public int getFinishTurn() {
        return finishTurn;
    }

    public void setFinishTurn(int finishTurn) {
        this.finishTurn = finishTurn;
    }

    public int getFinishPosition() {
        return finishPosition;
    }

    public void setFinishPosition(int finishPosition) {
        this.finishPosition = finishPosition;
    }


    public int getBurnoutCount() {
        return burnoutCount;
    }

    public ArrayList<Integer> getBurnoutTurns() {
        return burnoutTurns;
    }

    public void setStartingAccuracy(double startingAccuracy) {
        this.startingAccuracy = startingAccuracy;
    }


}