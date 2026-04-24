import java.awt.Color;

public class TypistConfig {
    private String typingStyle;



    public TypistConfig() {
        typingStyle = "Touch Typist";


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


        return bonus;
    }

    public double getSpeedMultiplier() {
        double multiplier = 1.0;

        if (typingStyle.equals("Touch Typist")) {
            multiplier *= 1.10;
        } else if (typingStyle.equals("Hunt & Peck")) {
            multiplier *= 0.80;
        } else if (typingStyle.equals("Phone Thumbs")) {
            multiplier *= 0.95;
        } else if (typingStyle.equals("Voice-to-Text")) {
            multiplier *= 1.25;
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


    public String getTypingStyle() {
        return typingStyle;
    }

    public void setTypingStyle(String typingStyle) {
        this.typingStyle = typingStyle;
    }


}