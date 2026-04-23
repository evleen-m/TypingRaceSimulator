import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Builds and manages the race display panel. * Uses composition instead of extending JPanel.
 */
public class RacePanel {
    private Game race;
    private JPanel mainPanel;
    private JPanel drawingPanel;

    public RacePanel(Game game) {
        race = game;
        buildPanel();
    }

    /**
     * Builds the panel structure.
     */
    private void buildPanel() {
        mainPanel = new JPanel(new BorderLayout());
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRace(g);
            }
        };
        drawingPanel.setBackground(new Color(245, 247, 250));
        mainPanel.add(drawingPanel, BorderLayout.CENTER);
    }

    /**
     * Returns the main race panel. * * @return the race panel
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * Repaints the drawing area.
     */
    public void repaintRace() {
        drawingPanel.repaint();
    }

    /**
     * Draws the full race scene. * * @param g the graphics object
     */
    private void drawRace(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(35, 35, 35));
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.drawString("Typing Race", 25, 40);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.drawString("Passage length: " + race.getPassageLength() + " chars", 25, 70);
        g2.drawString("Turns: " + race.getTurnCount(), 25, 92);
        Typist[] seats = race.getSeats();
        int y = 140;
        for (int i = 0; i < race.getNumberOfSeats(); i++) {
            drawLane(g2, seats[i], i, y);
            y += 145;
        }
        if (race.isFinished() && race.getWinner() != null) {
            g2.setColor(new Color(0, 130, 60));
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.drawString("And the winner is... " + race.getWinner().getName() + "!", 25, 590);
            g2.drawString("Final accuracy: " + String.format("%.2f", race.getWinner().getAccuracy()), 25, 620);
        }
    }

    /**
     * Draws one typist lane showing progress through the actual passage text. * * @param g2 the graphics object * @param typist the typist in the lane * @param laneNumber the lane number * @param y the vertical position of the lane
     */
    private void drawLane(Graphics2D g2, Typist typist, int laneNumber, int y) {
        int x = 25;
        int width = 900;
        int height = 85;
        laneNumber++;

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, width, height, 18, 18);
        g2.setColor(new Color(210, 210, 210));
        g2.drawRoundRect(x, y, width, height, 18, 18);
        g2.setColor(new Color(70, 70, 70));
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.drawString("Lane " + laneNumber, x, y - 8);
        if (typist == null) {
            g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
            g2.drawString("Empty seat", x + 15, y + 35);
            return;
        }
        String passage = race.getPassageText();
        int progress = typist.getProgress();
        if (progress < 0) {
            progress = 0;
        }
        if (progress > passage.length()) {
            progress = passage.length();
        }
        String completed = passage.substring(0, progress);
        String remaining = passage.substring(progress);
        int textX = x + 20;
        int textY = y + 38;
        g2.setFont(new Font("Monospaced", Font.PLAIN, 18));
        g2.setColor(new Color(60, 140, 255));
        g2.drawString(completed, textX, textY);
        int completedWidth = g2.getFontMetrics().stringWidth(completed);
        g2.setColor(new Color(130, 130, 130));
        g2.drawString(remaining, textX + completedWidth, textY);
        int cursorX = textX + completedWidth; // circle position and size
        int circleDiameter = 16;
        int circleX = cursorX;
        int circleY = y + 7; // draw coloured circle
        g2.setColor(typist.getColor());
        g2.fillOval(circleX, circleY, circleDiameter, circleDiameter); // draw unicode symbol on top
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Dialog", Font.BOLD, 16));
        g2.drawString(String.valueOf(typist.getSymbol()), cursorX, y + 21);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.setColor(new Color(40, 40, 40));
        g2.drawString(typist.getName() + " | Accuracy: " + String.format("%.2f", typist.getAccuracy()) + " | WPM: " + String.format("%.2f", race.calculateWPM(typist)), x + 15, y + 68);
        if (typist.isBurntOut()) {
            g2.setColor(new Color(190, 50, 50));
            g2.drawString("BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)", x + 520, y + 68);
        } else if (typist.hasJustMistyped()) {
            g2.setColor(new Color(210, 120, 0));
            g2.drawString("Just mistyped", x + 520, y + 68);
        } else if (race.getWinner() != null) {
            if (Objects.equals(race.getWinner().getName(), typist.getName())) {
                g2.setColor(new Color(0, 250, 0));
                typist.setColor(Color.GREEN);
                g2.drawString("WON", x + 520, y + 68);
            }
        }
    }
}