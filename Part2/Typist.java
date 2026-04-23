import java.awt.*;

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
    private double accuracyRating;
    private boolean justMistyped;
    private Color color;

    /**
     * Creates a new typist with a given symbol, name, and accuracy rating.
     *
     * @param typistSymbol a single Unicode character representing this typist
     * @param typistName the name of the typist
     * @param typistAccuracy the typist's accuracy rating, between 0.0 and 1.0
     */
    public Typist(char typistSymbol, String typistName, double typistAccuracy)
    {
        this.typistName = typistName;
        this.typistSymbol = typistSymbol;
        this.progress = 0;
        this.burntOut = false;
        this.burntOutTurnsRemaining = 0;
        this.accuracyRating = typistAccuracy;
        this.justMistyped = false;
        setRandColor();
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

    private void setRandColor()
    {
        int r = (int)(Math.random() * 256);
        int g = (int)(Math.random() * 156);
        int b = (int)(Math.random() * 256);
        color = new Color(r, g, b);
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }
}