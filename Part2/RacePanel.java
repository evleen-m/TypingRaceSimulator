import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;

/**
 * A custom JPanel used to visually render the typing race.
 *
 * This class is responsible for:
 * - Drawing each typist's lane
 * - Displaying typist position (progress)
 * - Showing accuracy, WPM, and state (burnout/mistype)
 * - Displaying the winner when the race finishes
 *
 * @author Evleen Manghera
 * @version 1
 */
public class RacePanel extends JPanel
{
    private TypingRace race;

    /**
     * Constructs the panel with a reference to the race.
     * @param race the TypingRace instance to display
     */
    public RacePanel(TypingRace race)
    {
        this.race = race;
        setBackground(new Color(245, 247, 250)); // light background colour
    }

    /**
     * Paints the entire race view.
     * This method is automatically called whenever the panel is repainted.
     * @param g the Graphics object used for drawing
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        //redraw (clear) panel
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // allows for smoother graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw title
        g2.setColor(new Color(35, 35, 35));
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.drawString("Typing Race", 25, 40);

        // draw race info
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.drawString("Passage length: " + race.getPassageLength() + " chars", 25, 70);
        g2.drawString("Turns: " + race.getTurnCount(), 25, 92);

        // draw each typist lane
        Typist[] seats = race.getSeats();
        int y = 130;

        for (int i = 1; i < race.getNumberOfSeats(); i++)
        {
            drawLane(g2, seats[i], i, y);
            y += 120;
        }

        // display winner if race finished
        if (race.isFinished() && race.getWinner() != null)
        {
            g2.setColor(new Color(0, 130, 60));
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.drawString("And the winner is... " + race.getWinner().getName() + "!", 25, 520);
            g2.drawString("Final accuracy: " + String.format("%.2f", race.getWinner().getAccuracy()), 25, 550);
        }
    }

    /**
     * Draws a single typist lane.
     *
     * @param g2 the Graphics2D object
     * @param typist the typist to display
     * @param laneNumber the lane index
     * @param y the vertical position of the lane
     */
    private void drawLane(Graphics2D g2, Typist typist, int laneNumber, int y)
    {
        int x = 25;
        int width = 740;
        int height = 50;

        // draw lane background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, width, height, 18, 18);

        // draw lane border
        g2.setColor(new Color(210, 210, 210));
        g2.drawRoundRect(x, y, width, height, 18, 18);

        // lane label
        g2.setColor(new Color(70, 70, 70));
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.drawString("Lane " + laneNumber, x, y - 8);

        // if no typist in this seat
        if (typist == null)
        {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g2.drawString("Empty seat", x + 15, y + 30);
            return;
        }

        // calculate marker position based on progress
        int markerAreaStart = x + 15;
        int markerAreaWidth = width - 30;

        int progress = typist.getProgress();
        if (progress > race.getPassageLength())
        {
            progress = race.getPassageLength();
        }

        double fraction = (double) progress / race.getPassageLength();
        int markerX = markerAreaStart + (int)(fraction * (markerAreaWidth - 30));

        // draw typist marker
        g2.setColor(typist.getColor());
        g2.fillOval(markerX, y + 10, 30, 30);

        // draw typist symbol inside marker
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString(String.valueOf(typist.getSymbol()), markerX + 8, y + 30);

        // draw typist info (name, accuracy, WPM)
        g2.setColor(new Color(40, 40, 40));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.drawString(
                typist.getName()
                        + " | Accuracy: " + String.format("%.2f", typist.getAccuracy())
                        + " | WPM: " + String.format("%.2f", race.calculateWPM(typist)),
                x + 10, y + 78
        );

        // show burnout state
        if (typist.isBurntOut())
        {
            g2.setColor(new Color(190, 50, 50));
            g2.drawString("BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)", x + 450, y + 78);
        }
        // show mistype indicator
        else if (typist.hasJustMistyped())
        {
            g2.setColor(new Color(210, 120, 0));
            g2.drawString("Just mistyped", x + 450, y + 78);
        }
    }
}