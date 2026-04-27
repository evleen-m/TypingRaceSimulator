import java.awt.Color;

public class TypistConfig {
    private String typingStyle;
    private String keyboardType;

    private String symbol;
    private Color colour;

    private boolean wristSupport;
    private boolean energyDrink;
    private boolean headphones;

    public TypistConfig() {
        // default values can be changed
        typingStyle = "Touch Typist";
        keyboardType = "Membrane";

        symbol = "Ⓐ";
        colour = Color.BLUE;

        wristSupport = false;
        energyDrink = false;
        headphones = false;
    }

    /**
     * calculates the accuracy bonus (or penalty) based on typing style
     * and whether energy drink is active during the race
     *
     * @param passageLength total length of the passage
     * @param progress current progress through the passage
     * @return accuracy bonus value
     */
    public double getAccuracyBonus(int passageLength, int progress)
    {
        double bonus = 0;

        // adjusts bonus based on typing style
        switch (typingStyle) {
            case "Touch Typist" -> bonus += 0.06;
            case "Hunt & Peck" -> bonus -= 0.05;
            case "Phone Thumbs" -> bonus -= 0.08;
            case "Voice-to-Text" -> bonus += 0.08;
        }

        // energy drink gives early boost but worse later
        if (energyDrink) {
            if (progress < passageLength / 2) {
                bonus += 0.16;
            } else {
                bonus -= 0.16;
            }
        }

        return bonus;
    }

    /**
     * calculates the typing speed multiplier based on keyboard type
     * different keyboards affect how fast the typist can type
     *
     * @return speed multiplier value
     */
    public double getSpeedMultiplier()
    {
        double multiplier = 1.0;

        // adjust speed depending on keyboard type
        switch (keyboardType) {
            case "Mechanical" -> multiplier *= 1.15;
            case "Membrane" -> multiplier *= 0.80;
            case "Touchscreen" -> multiplier *= 0.95;
            case "Stenography" -> multiplier *= 1.40;
        }

        return multiplier;
    }

    /**
     * calculates the mistype multiplier based on keyboard type
     * and whether headphones are used
     * lower multiplier means fewer mistakes
     *
     * @return mistype multiplier value
     */
    public double getMistypeMultiplier()
    {
        double multiplier = 1.0;

        // adjust mistype rate using switch for keyboard type
        switch (keyboardType)
        {
            case "Mechanical" -> multiplier *= 1.30;
            case "Membrane" -> multiplier *= 0.80;
            case "Touchscreen" -> multiplier *= 1.05;
            case "Stenography" -> multiplier *= 1.80;
        }

        // headphones reduce mistakes
        if (headphones)
        {
            multiplier *= 0.85; // fewer mistypes
        }

        return multiplier;
    }

    /**
     * calculates the burnout multiplier based on typing style
     * lower multiplier means less chance of burnout
     *
     * @return burnout multiplier value
     */
    public double getBurnoutMultiplier()
    {
        double multiplier = 1.0;

        switch (typingStyle) {
            case "Touch Typist" -> multiplier *= 1.10;
            case "Hunt & Peck" -> multiplier *= 0.95;
            case "Phone Thumbs" -> multiplier *= 0.80;
            case "Voice-to-Text" -> multiplier *= 1.20;
        }

        return multiplier;
    }



    /**
     * returns a modifier for burnout duration based on accessories
     * wrist support reduces how long burnout lasts
     *
     * @return burnout duration modifier value
     */
    public int getBurnoutDurationModifier()
    {
        if (wristSupport) {
            return -1;
        }

        return 0;
    }

    public String getTypingStyle() {
        return typingStyle;
    }

    public void setTypingStyle(String typingStyle) {
        this.typingStyle = typingStyle;
    }

    public String getKeyboardType() {
        return keyboardType;
    }

    public void setKeyboardType(String keyboardType) {
        this.keyboardType = keyboardType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public void setWristSupport(boolean wristSupport) {
        this.wristSupport = wristSupport;
    }

    public void setEnergyDrink(boolean energyDrink) {
        this.energyDrink = energyDrink;
    }

    public void setHeadphones(boolean headphones) {
        this.headphones = headphones;
    }

}