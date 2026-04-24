import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Objects;

/**
 * Builds and manages the race display panel.
 * Uses composition instead of extending JPanel.
 */
public class RacePanel
{
    private Game race;
    private JPanel mainPanel;
    private JPanel lanesPanel;
    private JScrollPane scrollPane;

    public RacePanel(Game game)
    {
        race = game;
        buildPanel();
    }

    /**
     * Builds the panel structure.
     */
    private void buildPanel()
    {
        mainPanel = new JPanel(new BorderLayout());

        lanesPanel = new JPanel();
        lanesPanel.setLayout(new BoxLayout(lanesPanel, BoxLayout.Y_AXIS));
        lanesPanel.setBackground(new Color(245, 247, 250));

        scrollPane = new JScrollPane(lanesPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        buildRaceDisplay();
    }

    /**
     * Returns the main race panel.
     *
     * @return the race panel
     */
    public JPanel getPanel()
    {
        return mainPanel;
    }

    /**
     * Repaints the race display.
     */
    public void repaintRace()
    {
        int scrollPosition = scrollPane.getVerticalScrollBar().getValue();

        buildRaceDisplay();

        lanesPanel.revalidate();
        lanesPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPosition);
        });
    }

    /**
     * Builds the full race scene.
     */
    private void buildRaceDisplay()
    {
        lanesPanel.removeAll();

        JLabel title = new JLabel("Typing Race");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 25, 5, 25));
        lanesPanel.add(title);

        JLabel passageLength = new JLabel("Passage length: " + race.getPassageLength() + " chars");
        passageLength.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passageLength.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        lanesPanel.add(passageLength);

        JLabel turns = new JLabel("Turns: " + race.getTurnCount());
        turns.setFont(new Font("SansSerif", Font.PLAIN, 16));
        turns.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
        lanesPanel.add(turns);

        JLabel modifiers = new JLabel("Modifiers On: " + race.getModifiers());
        modifiers.setFont(new Font("SansSerif", Font.PLAIN, 12));
        modifiers.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
        lanesPanel.add(modifiers);

        Typist[] seats = race.getSeats();

        for (int i = 0; i < race.getNumberOfSeats(); i++)
        {
            lanesPanel.add(buildLane(seats[i], i));
        }

        if (race.isFinished() && race.getWinner() != null)
        {
            JLabel winner = new JLabel("And the winner is... " + race.getWinner().getName() + "!");
            winner.setForeground(new Color(0, 130, 60));
            winner.setFont(new Font("SansSerif", Font.BOLD, 22));
            winner.setBorder(BorderFactory.createEmptyBorder(20, 25, 5, 25));
            lanesPanel.add(winner);

            JLabel accuracy = new JLabel("Final accuracy: " + String.format("%.2f", race.getWinner().getAccuracy()));
            accuracy.setForeground(new Color(0, 130, 60));
            accuracy.setFont(new Font("SansSerif", Font.BOLD, 22));
            accuracy.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
            lanesPanel.add(accuracy);
        }
    }

    /**
     * Builds one typist lane showing progress through the actual passage text.
     *
     * @param typist the typist in the lane
     * @param laneNumber the lane number
     * @return the lane panel
     */
    private JPanel buildLane(Typist typist, int laneNumber)
    {
        JPanel lanePanel = new JPanel(new BorderLayout(10, 10));
        lanePanel.setBackground(Color.WHITE);
        lanePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 25, 10, 25),
                BorderFactory.createLineBorder(new Color(210, 210, 210))
        ));

        laneNumber++;

        JPanel topPanel = new JPanel(null);
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(1000, 65));

        JLabel laneLabel = new JLabel("Lane " + laneNumber);
        laneLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        laneLabel.setBounds(10, 5, 100, 20);
        topPanel.add(laneLabel);

        if (typist == null)
        {
            JTextPane emptyPane = new JTextPane();
            emptyPane.setEditable(false);
            emptyPane.setText("Empty seat");
            emptyPane.setFont(new Font("Monospaced", Font.PLAIN, 16));
            emptyPane.setBackground(Color.WHITE);

            lanePanel.add(topPanel, BorderLayout.NORTH);
            lanePanel.add(emptyPane, BorderLayout.CENTER);

            return lanePanel;
        }

        JLabel iconLabel = getJLabel(typist);
        topPanel.add(iconLabel);

        JLabel nameLabel = new JLabel(
                typist.getName()
                        + " | Accuracy: " + String.format("%.2f", typist.getAccuracy())
                        + " | WPM: " + String.format("%.2f", race.calculateWPM(typist))
        );

        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        nameLabel.setBounds(10, 35, 520, 20);
        topPanel.add(nameLabel);

        if (typist.isBurntOut())
        {
            JLabel burnout = new JLabel("BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)");
            burnout.setForeground(new Color(190, 50, 50));
            burnout.setBounds(540, 35, 300, 20);
            topPanel.add(burnout);
        }
        else if (typist.hasJustMistyped())
        {
            JLabel mistyped = new JLabel("Just mistyped");
            mistyped.setForeground(new Color(210, 120, 0));
            mistyped.setBounds(540, 35, 300, 20);
            topPanel.add(mistyped);
        }
        else if (race.getWinner() != null)
        {
            if (Objects.equals(race.getWinner().getName(), typist.getName()))
            {
                JLabel won = new JLabel("YOU WIN");
                won.setForeground(new Color(0, 250, 0));
                won.setBounds(540, 35, 300, 20);
                typist.setColor(Color.GREEN);
                topPanel.add(won);
            }
            else{
                JLabel lost = new JLabel("YOU LOST");
                lost.setForeground(new Color(250, 0, 0));
                lost.setBounds(540, 35, 300, 20);
                typist.setColor(Color.RED);
                topPanel.add(lost);
            }
        }

        JTextPane passagePane = new JTextPane();
        passagePane.setEditable(false);
        passagePane.setFont(new Font("Monospaced", Font.PLAIN, 18));
        passagePane.setBackground(Color.WHITE);
        passagePane.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        addStyledPassageText(passagePane, typist);

        lanePanel.add(topPanel, BorderLayout.NORTH);
        lanePanel.add(passagePane, BorderLayout.CENTER);

        return lanePanel;
    }

    private JLabel getJLabel(Typist typist) {
        JLabel iconLabel = new JLabel(String.valueOf(typist.getSymbol()), SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(typist.getColor());
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        int progress = typist.getProgress();
        int passageLength = race.getPassageText().length();

        if (progress < 0)
        {
            progress = 0;
        }

        if (progress > passageLength)
        {
            progress = passageLength;
        }

        double progressPercent = 0;

        if (passageLength > 0)
        {
            progressPercent = (double) progress / passageLength;
        }

        int movementStartX = 110;
        int movementWidth = 800;
        int iconWidth = 30;
        int iconHeight = 25;

        int iconX = movementStartX + (int) (progressPercent * (movementWidth - iconWidth));

        iconLabel.setBounds(iconX, 5, iconWidth, iconHeight);
        return iconLabel;
    }

    /**
     * Adds styled passage text with correct characters coloured and cursor shown.
     *
     * @param passagePane the text pane to display the passage
     * @param typist the typist in the lane
     */
    private void addStyledPassageText(JTextPane passagePane, Typist typist)
    {
        String passage = race.getPassageText();
        int progress = typist.getProgress();

        if (progress < 0)
        {
            progress = 0;
        }

        if (progress > passage.length())
        {
            progress = passage.length();
        }

        String completed = passage.substring(0, progress);
        String remaining = passage.substring(progress);

        StyledDocument document = passagePane.getStyledDocument();

        Style correctStyle = passagePane.addStyle("Correct", null);
        StyleConstants.setForeground(correctStyle, new Color(60, 140, 255));

        Style cursorStyle = passagePane.addStyle("Cursor", null);
        StyleConstants.setForeground(cursorStyle, Color.BLACK);
        StyleConstants.setBold(cursorStyle, true);

        Style remainingStyle = passagePane.addStyle("Remaining", null);
        StyleConstants.setForeground(remainingStyle, new Color(130, 130, 130));

        try
        {
            document.insertString(document.getLength(), completed, correctStyle);
            document.insertString(document.getLength(), "|", cursorStyle);
            document.insertString(document.getLength(), remaining, remainingStyle);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }
}