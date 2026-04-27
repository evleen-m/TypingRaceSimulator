import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Draws a line graph showing WPM over time for each typist
 */
public class WpmLineGraphPanel extends JPanel {
    private TypingRace race;

    public WpmLineGraphPanel(TypingRace race) {
        this.race = race;
        setBorder(BorderFactory.createTitledBorder("WPM Over Race"));
        setBackground(Color.WHITE);
    }

    /**
     * paints the wpm graph onto the component
     * draws the axis, labels and a line for each typist
     *
     * @param g the graphics object used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Typist[] typists = race.getSeats();
        RaceStats stats = race.getRaceStats();

        // stops drawing if there is no data
        if (typists == null || typists.length == 0 || stats == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        int leftPadding = 70;
        int bottomPadding = 60;
        int topPadding = 40;
        int rightPadding = 30;

        int graphWidth = width - leftPadding - rightPadding;
        int graphHeight = height - topPadding - bottomPadding;

        int maxTurns = Math.max(1, stats.getLongestHistory());
        double maxWpm = 1;

        // finds highest wpm so the graph scales properly
        for (int i = 0; i < typists.length; i++) {
            if (typists[i] != null) {
                ArrayList<Double> history = stats.getWpmHistory(typists[i].getName());

                for (int j = 0; j < history.size(); j++) {
                    maxWpm = Math.max(maxWpm, history.get(j));
                }
            }
        }

        // draws graph axis
        g2.setColor(Color.BLACK);
        g2.drawLine(leftPadding, topPadding, leftPadding, topPadding + graphHeight);
        g2.drawLine(leftPadding, topPadding + graphHeight, leftPadding + graphWidth, topPadding + graphHeight);

        // label axis
        g2.drawString("WPM", 20, topPadding + 10);
        g2.drawString("Turns", leftPadding + graphWidth - 30, topPadding + graphHeight + 40);

        // draw y-axis numbers and guide lines
        for (int i = 0; i <= 5; i++) {
            int y = topPadding + graphHeight - (i * graphHeight / 5);
            double labelValue = maxWpm * i / 5.0;

            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(leftPadding, y, leftPadding + graphWidth, y);

            g2.setColor(Color.BLACK);
            g2.drawString(String.format("%.0f", labelValue), 25, y + 5);
        }

        // draw x-axis turn labels
        for (int i = 0; i <= 5; i++) {
            int x = leftPadding + (i * graphWidth / 5);
            int turnLabel = (int) ((double) maxTurns * i / 5.0);

            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(turnLabel), x - 5, topPadding + graphHeight + 20);
        }

        // draws each typist wpm line
        for (int i = 0; i < typists.length; i++) {
            if (typists[i] != null) {
                ArrayList<Double> history = stats.getWpmHistory(typists[i].getName());

                if (history.size() < 2) {
                    continue;
                }

                Color typistColor = typists[i].getColor();

                // default colour if typist doesnt have one ->
                if (typistColor == null) {
                    typistColor = new Color(60, 140, 255);
                }

                g2.setColor(typistColor);
                g2.setStroke(new BasicStroke(3));

                for (int j = 1; j < history.size(); j++) {
                    int x1 = leftPadding + (int) (((double) (j - 1) / Math.max(1, maxTurns - 1)) * graphWidth);
                    int y1 = topPadding + graphHeight - (int) ((history.get(j - 1) / maxWpm) * graphHeight);

                    int x2 = leftPadding + (int) (((double) j / Math.max(1, maxTurns - 1)) * graphWidth);
                    int y2 = topPadding + graphHeight - (int) ((history.get(j) / maxWpm) * graphHeight);

                    g2.drawLine(x1, y1, x2, y2);
                }

                // put typist symbol at the end of the line
                double lastWpm = history.get(history.size() - 1);
                int endX = leftPadding + (int) (((double) (history.size() - 1) / Math.max(1, maxTurns - 1)) * graphWidth);
                int endY = topPadding + graphHeight - (int) ((lastWpm / maxWpm) * graphHeight);

                g2.drawString(String.valueOf(typists[i].getSymbol()), endX + 5, endY);
            }
        }
    }
}