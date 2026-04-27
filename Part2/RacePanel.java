import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Objects;

/**
 * builds and manages the race display panel
 */
public class RacePanel {
    private TypingRace race;
    private JPanel mainPanel;
    private JPanel lanesPanel;
    private JScrollPane scrollPane;

    public RacePanel(TypingRace game) {
        race = game;
        buildPanel();
    }

    /**
     * builds the main panel structure
     */
    private void buildPanel() {
        mainPanel = new JPanel(new BorderLayout());

        lanesPanel = new JPanel();
        lanesPanel.setLayout(new BoxLayout(lanesPanel, BoxLayout.Y_AXIS));
        lanesPanel.setBackground(new Color(245, 247, 250));

        scrollPane = new JScrollPane(lanesPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        buildRaceDisplay();
    }

    /**
     * builds the full race display with title info and lanes
     */
    private void buildRaceDisplay() {
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

        // builds one lane for each seat
        for (int i = 0; i < race.getNumberOfSeats(); i++) {
            lanesPanel.add(buildLane(seats[i], i));
        }

        // shows winner info after race has finished
        if (race.isFinished() && race.getWinner() != null) {
            JLabel winner = new JLabel("And the winner is... " + race.getWinner().getName() + "!");
            winner.setForeground(new Color(0, 130, 60));
            winner.setFont(new Font("SansSerif", Font.BOLD, 22));
            winner.setBorder(BorderFactory.createEmptyBorder(20, 25, 5, 25));
            lanesPanel.add(winner);

            JLabel accuracy = new JLabel("Final accuracy: " + String.format("%.2f", race.getWinner().getAccuracy())
                    + " changed from: " + String.format("%.2f", race.getWinner().getINITAL_ACCURACY()));
            accuracy.setForeground(new Color(0, 130, 60));
            accuracy.setFont(new Font("SansSerif", Font.BOLD, 22));
            accuracy.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
            lanesPanel.add(accuracy);
        }
    }

    /**
     * builds one typist lane showing progress through the passage text
     *
     * @param typist     the typist in the lane
     * @param laneNumber the lane number
     * @return the lane panel
     */
    private JPanel buildLane(Typist typist, int laneNumber) {
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

        // handles empty seats before using typist data
        if (typist == null) {
            JTextPane emptyPane = new JTextPane();
            emptyPane.setEditable(false);
            emptyPane.setText("Empty seat");
            emptyPane.setFont(new Font("Monospaced", Font.PLAIN, 16));
            emptyPane.setBackground(Color.WHITE);

            lanePanel.add(topPanel, BorderLayout.NORTH);
            lanePanel.add(emptyPane, BorderLayout.CENTER);

            return lanePanel;
        }

        JLabel nameLabel = new JLabel(typist.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(typist.getColor());
        nameLabel.setBounds(150, 5, 500, 20);
        topPanel.add(nameLabel);

        JLabel iconLabel = getJLabel(typist);
        topPanel.add(iconLabel);

        JLabel statsLabel = new JLabel(
                "TYPE: " + typist.getConfig().getTypingStyle()
                        + " | KEYBOARD: " + typist.getConfig().getKeyboardType()
                        + " | Accuracy: " + String.format("%.2f", typist.getAccuracy())
                        + " | WPM: " + String.format("%.2f", race.calculateWPM(typist))
        );

        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        statsLabel.setBounds(10, 35, 700, 20);
        topPanel.add(statsLabel);

        // shows current typist status
        if (typist.isBurntOut()) {
            JLabel burnout = new JLabel("BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)");
            burnout.setFont(new Font("SansSerif", Font.BOLD, 15));
            burnout.setForeground(new Color(190, 50, 50));
            burnout.setBounds(540, 10, 300, 20);
            topPanel.add(burnout);
        } else if (typist.hasJustMistyped()) {
            JLabel mistyped = new JLabel("Just mistyped");
            mistyped.setFont(new Font("SansSerif", Font.BOLD, 15));
            mistyped.setForeground(new Color(210, 120, 0));
            mistyped.setBounds(540, 10, 300, 20);
            topPanel.add(mistyped);
        } else if (race.getWinner() != null) {
            if (Objects.equals(race.getWinner().getName(), typist.getName())) {
                JLabel won = new JLabel("YOU WIN");
                won.setFont(new Font("SansSerif", Font.BOLD, 15));
                won.setForeground(new Color(0, 250, 0));
                won.setBounds(540, 10, 300, 20);
                typist.setColor(Color.BLACK);
                topPanel.add(won);
            } else if (typist.isFinished()) {
                typist.setColor(Color.BLACK);
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

    /**
     * creates the typist symbol label shown in the lane
     *
     * @param typist the typist to make an icon for
     * @return label containing the typist symbol
     */
    private JLabel getJLabel(Typist typist) {
        JLabel iconLabel = new JLabel(String.valueOf(typist.getSymbol()), SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(typist.getColor());
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        int iconWidth = 25;
        int iconHeight = 25;

        iconLabel.setBounds(100, 5, iconWidth, iconHeight);

        return iconLabel;
    }

    /**
     * adds styled passage text with completed text highlighted and cursor shown
     *
     * @param passagePane the text pane to display the passage
     * @param typist      the typist in the lane
     */
    private void addStyledPassageText(JTextPane passagePane, Typist typist) {
        String passage = race.getPassageText();
        int progress = typist.getProgress();

        // makes sure progress is inside passage range
        if (progress < 0) {
            progress = 0;
        }

        if (progress > passage.length()) {
            progress = passage.length();
        }

        String completed = passage.substring(0, progress);
        String remaining = passage.substring(progress);

        StyledDocument document = passagePane.getStyledDocument();

        Style correctStyle = passagePane.addStyle("Correct", null);

        Color typistColor = typist.getColor();

        // fallback in case typist colour is null
        if (typistColor == null) {
            typistColor = new Color(60, 140, 255);
        }

        // completed text uses typist colour
        StyleConstants.setBackground(correctStyle, typistColor);
        StyleConstants.setForeground(correctStyle, Color.WHITE);
        StyleConstants.setBold(correctStyle, true);

        Style cursorStyle = passagePane.addStyle("Cursor", null);
        StyleConstants.setForeground(cursorStyle, typistColor);
        StyleConstants.setBold(cursorStyle, true);

        Style remainingStyle = passagePane.addStyle("Remaining", null);
        StyleConstants.setForeground(remainingStyle, new Color(130, 130, 130));

        try {
            document.insertString(document.getLength(), completed, correctStyle);
            document.insertString(document.getLength(), "|", cursorStyle);
            document.insertString(document.getLength(), remaining, remainingStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * rebuilds and repaints the race display
     * keeps the current scroll position
     */
    public void repaintRace() {
        int scrollPosition = scrollPane.getVerticalScrollBar().getValue();

        buildRaceDisplay();

        lanesPanel.revalidate();
        lanesPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPosition);
        });
    }

    /**
     * returns the main race panel
     *
     * @return the main race panel
     */
    public JPanel getPanel() {
        return mainPanel;
    }
}