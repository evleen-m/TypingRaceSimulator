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
        typingStyle = "Touch Typist";
        keyboardType = "Membrane";

        symbol = "Ⓐ";
        colour = Color.BLUE;

        wristSupport = false;
        energyDrink = false;
        headphones = false;
    }

    public double getAccuracyBonus(int passageLength, int progress) {
        double bonus = 0;

        if (typingStyle.equals("Touch Typist")) {
            bonus += 0.06;
        } else if (typingStyle.equals("Hunt & Peck")) {
            bonus -= 0.05;
        } else if (typingStyle.equals("Phone Thumbs")) {
            bonus -= 0.03;
        } else if (typingStyle.equals("Voice-to-Text")) {
            bonus += 0.08;
        }

        if (energyDrink) {
            if (progress < passageLength / 2) {
                bonus += 0.16;
            } else {
                bonus -= 0.16;
            }
        }

        return bonus;
    }

    public double getSpeedMultiplier() {
        double multiplier = 1.0;

        if (keyboardType.equals("Mechanical")) {
            multiplier *= 1.15;
        } else if (keyboardType.equals("Membrane")) {
            multiplier *= 1.00;
        } else if (keyboardType.equals("Touchscreen")) {
            multiplier *= 0.90;
        } else if (keyboardType.equals("Stenography")) {
            multiplier *= 1.40;
        }

        return multiplier;
    }

    public double getMistypeMultiplier() {
        double multiplier = 1.0;

        if (keyboardType.equals("Mechanical")) {
            multiplier *= 0.90;
        } else if (keyboardType.equals("Membrane")) {
            multiplier *= 1.00;
        } else if (keyboardType.equals("Touchscreen")) {
            multiplier *= 1.25;
        } else if (keyboardType.equals("Stenography")) {
            multiplier *= 1.10;
        }

        if (headphones) {
            multiplier *= 0.70; // fewer mistypes
        }

        return multiplier;
    }

    public double getBurnoutMultiplier() {
        double multiplier = 1.0;

        if (typingStyle.equals("Touch Typist")) {
            multiplier *= 0.90;
        } else if (typingStyle.equals("Hunt & Peck")) {
            multiplier *= 0.80;
        } else if (typingStyle.equals("Phone Thumbs")) {
            multiplier *= 1.20;
        } else if (typingStyle.equals("Voice-to-Text")) {
            multiplier *= 1.10;
        }

        return multiplier;
    }



    public int getBurnoutDurationModifier() {
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

    public boolean hasWristSupport() {
        return wristSupport;
    }

    public void setWristSupport(boolean wristSupport) {
        this.wristSupport = wristSupport;
    }

    public boolean hasEnergyDrink() {
        return energyDrink;
    }

    public void setEnergyDrink(boolean energyDrink) {
        this.energyDrink = energyDrink;
    }

    public boolean hasHeadphones() {
        return headphones;
    }

    public void setHeadphones(boolean headphones) {
        this.headphones = headphones;
    }

}