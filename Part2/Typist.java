import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents a typist in the typing race.
 *
 * @author Evleen Manghera
 * @version 1
 */
public class Typist
{
    private String typistName;
    private char typistSymbol;
    private int progress;
    private Boolean burntOut;
    private int burntOutTurnsRemaining;
    private final double INITAL_ACCURACY;
    private double accuracyRating;
    private boolean justMistyped;
    private Color color;
    private boolean finished = false;

    private TypistConfig config;



    // stats vars
//    private int correctKeystrokes = 0;
//    private int mistypes = 0;
//    private int burnoutCount = 0;
    private double startingAccuracy;
    private int finishTurn = 0;
    private int finishPosition = 0;


    /**
     * Creates a new typist with a given symbol, name, and accuracy rating.
     *
     * @param typistSymbol a single Unicode character representing this typist
     */
    public Typist(String name,char typistSymbol)
    {
        typistName = name;
        this.typistSymbol = typistSymbol;
        progress = 0;
        burntOut = false;
        burntOutTurnsRemaining = 0;
        accuracyRating = randTypistAccuracy();
        INITAL_ACCURACY = accuracyRating;
        justMistyped = false;
        startingAccuracy = accuracyRating;
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

    public double getINITAL_ACCURACY() {
        return INITAL_ACCURACY;
    }

    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setJustMistyped(boolean justMistyped)
    {
        this.justMistyped = justMistyped;
    }

    public boolean hasJustMistyped()
    {
        return justMistyped;
    }

    /**
     * Sets this typist into a burnout state for a given number of turns.
     *
     * @param turns the number of turns the burnout will last
     */
    public void burnOut(int turns)
    {
        this.burntOut = true;
        this.burntOutTurnsRemaining = turns;
    }

    /**
     * Reduces the remaining burnout counter by one turn.
     * When the counter reaches zero, the typist recovers automatically.
     */
    public void recoverFromBurnout()
    {
        if (burntOut)
        {
            burntOutTurnsRemaining--;
            if (burntOutTurnsRemaining == 0)
            {
                burntOut = false;
            }
        }
    }

    public double getAccuracy()
    {
        return accuracyRating;
    }

    public int getProgress()
    {
        return progress;
    }

    public String getName()
    {
        return typistName;
    }

    public char getSymbol()
    {
        return typistSymbol;
    }

    public int getBurnoutTurnsRemaining()
    {
        return burntOutTurnsRemaining;
    }

    /**
     * Resets the typist to their initial state, ready for a new race.
     */
    public void resetToStart()
    {
        progress = 0;
        burntOut = false;
        burntOutTurnsRemaining = 0;
        justMistyped = false;
        finished = false;

        //reset start vars
//        correctKeystrokes = 0;
//        mistypes = 0;
//        burnoutCount = 0;
        finishTurn = 0;
        finishPosition = 0;
//        startingAccuracy = accuracyRating;

    }

    public boolean isBurntOut()
    {
        return burntOut;
    }

    /**
     * Advances the typist forward by one character.
     */
    public void typeCharacter()
    {
        progress += 1;
    }

    /**
     * Moves the typist backwards by a given number of characters.
     * Progress cannot go below zero.
     *
     * @param amount the number of characters to slide back
     */
    public void slideBack(int amount)
    {
        if (progress >= amount)
        {
            progress -= amount;
        }
        else
        {
            progress = 0;
        }
    }

    /**
     * Sets the accuracy rating of the typist.
     *
     * @param newAccuracy the new accuracy rating
     */
    public void setAccuracy(double newAccuracy)
    {
        if (newAccuracy < 0.0)
        {
            accuracyRating = 0.0;
        }
        else if (newAccuracy > 1.0)
        {
            accuracyRating = 1.0;
        }
        else
        {
            accuracyRating = newAccuracy;
        }
    }


    public void setColor(Color color)
    {
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }

    public TypistConfig getConfig() {
        return config;
    }

    public void setConfig(TypistConfig config) {
        this.config = config;
    }




    public int getFinishTurn()
    {
        return finishTurn;
    }

    public void setFinishTurn(int finishTurn)
    {
        this.finishTurn = finishTurn;
    }

    public int getFinishPosition()
    {
        return finishPosition;
    }

    public void setFinishPosition(int finishPosition)
    {
        this.finishPosition = finishPosition;
    }
}