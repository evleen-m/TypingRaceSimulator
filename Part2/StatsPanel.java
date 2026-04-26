import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * displays race statistics for each typist
 * uses tabs to seperate current race stats, history and comparison views
 */
public class StatsPanel {
    private FrameManager parentFrame;
    private TypingRace race;
    private JPanel mainPanel;

    public StatsPanel(FrameManager parentFrame, TypingRace race) {
        this.parentFrame = parentFrame;
        this.race = race;
    }

    /**
     * builds the main stats panel layout for the screen.
     * sets up the title at the top, tabs in the center,
     * and navigation buttons at the bottom.
     */
    public void buildPanel() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // title label at the top of the screen
        JLabel title = new JLabel("Typist Statistics");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));

        // adds components to the main layout
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(buildStatsSections(), BorderLayout.CENTER);
        mainPanel.add(buildButtons(), BorderLayout.SOUTH);
    }

    /**
     * builds the tabbed section that holds all stat views.
     * each tab shows different info like current race, history,
     * comparison and leaderboard.
     *
     * @return panel containing the tabbed stats area
     */
    private JPanel buildStatsSections() {
        JTabbedPane tabs = new JTabbedPane();

        // adds each stats section as a tab
        tabs.addTab("Current Race", buildCurrentRacePanel());
        tabs.addTab("History", buildHistoryPanel());
        tabs.addTab("Compare", buildComparisonPanel());
        tabs.addTab("Leaderboard", buildLeaderboardPanel());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    /**
     * builds the current race tab showing live stats and graph.
     * includes a scrollable list of typists on the left
     * and a wpm graph in the center.
     *
     * @return panel for the current race view
     */
    private JPanel buildCurrentRacePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));

        // scrollable list of typist stats
        JScrollPane scrollPane = new JScrollPane(buildWpmList());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(350, 400));

        // left side stats + center graph
        panel.add(scrollPane, BorderLayout.WEST);
        panel.add(new WpmLineGraphPanel(race), BorderLayout.CENTER);

        return panel;
    }

    /**
     * builds the list of typist stats shown on the left side.
     * shows wpm, position, accuracy and other info for each typist.
     *
     * @return panel containing the stats list
     */
    private JPanel buildWpmList() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // adds a border title for this section
        listPanel.setBorder(BorderFactory.createTitledBorder("Current Race Stats"));

        Typist[] typists = race.getSeats();
        RaceStats stats = race.getRaceStats();

        if (typists != null) {
            for (int i = 0; i < typists.length; i++) {
                if (typists[i] != null) {
                    // calculates current wpm for the typist
                    double wpm = race.calculateWPM(typists[i]);

                    JLabel nameLabel = new JLabel(typists[i].getName() + ": " + String.format("%.1f", wpm) + " WPM");

                    JLabel positionLabel = new JLabel("Finish Position: " + typists[i].getFinishPosition());
                    JLabel accuracyLabel = new JLabel("Accuracy: " + String.format("%.1f", typists[i].getAccuracyPercentage()) + "%");
                    JLabel burnoutLabel = new JLabel("Burnouts: " + typists[i].getBurnoutCount());
                    JLabel burnoutTurnsLabel = new JLabel("Burnout Turns: " + typists[i].getBurnoutTurns());
                    JLabel accuracyChangeLabel = new JLabel("Accuracy Change: " + String.format("%+.2f", typists[i].getAccuracyChange()));
                    JLabel personalBestLabel = new JLabel("Personal Best WPM: N/A");

                    // updates personal best if stats exist
                    if (stats != null) {
                        personalBestLabel.setText("Personal Best WPM: " + String.format("%.1f", stats.getPersonalBestWpm(typists[i].getName())));
                    }

                    // styling for labels
                    nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
                    nameLabel.setForeground(typists[i].getColor());
                    nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    positionLabel.setFont(positionLabel.getFont().deriveFont(13f));
                    accuracyLabel.setFont(accuracyLabel.getFont().deriveFont(13f));
                    burnoutLabel.setFont(burnoutLabel.getFont().deriveFont(13f));
                    burnoutTurnsLabel.setFont(burnoutTurnsLabel.getFont().deriveFont(13f));
                    accuracyChangeLabel.setFont(accuracyChangeLabel.getFont().deriveFont(13f));
                    personalBestLabel.setFont(personalBestLabel.getFont().deriveFont(13f));

                    positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    accuracyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    burnoutLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    burnoutTurnsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    accuracyChangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    personalBestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    // adds all labels to the panel
                    listPanel.add(nameLabel);
                    listPanel.add(positionLabel);
                    listPanel.add(accuracyLabel);
                    listPanel.add(burnoutLabel);
                    listPanel.add(burnoutTurnsLabel);
                    listPanel.add(accuracyChangeLabel);
                    listPanel.add(personalBestLabel);

                    // spacing between typists
                    listPanel.add(Box.createVerticalStrut(15));
                }
            }
        }

        return listPanel;
    }

    /**
     * builds the history tab showing past race results for each typist.
     * displays personal best and a list of previous races.
     *
     * @return panel containing the history view
     */
    private JPanel buildHistoryPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());

        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Typist[] typists = race.getSeats();
        RaceStats stats = race.getRaceStats();

        // if no data exists show a message
        if (typists == null || stats == null) {
            historyPanel.add(new JLabel("No history available."));
        } else {
            for (int i = 0; i < typists.length; i++) {
                if (typists[i] != null) {
                    JTextArea area = new JTextArea();
                    area.setEditable(false);
                    area.setFont(new Font("Monospaced", Font.PLAIN, 13));

                    // builds the text for each typist
                    String text = typists[i].getName() + "\n";
                    text += "Personal Best WPM: " + String.format("%.1f", stats.getPersonalBestWpm(typists[i].getName())) + "\n\n";

                    ArrayList<RaceRecord> history = stats.getRaceHistory(typists[i].getName());

                    if (history.isEmpty()) {
                        text += "No completed races recorded yet.\n";
                    }

                    // loops through each past race
                    for (int j = 0; j < history.size(); j++) {
                        RaceRecord record = history.get(j);

                        text += "Race " + record.getRaceNumber() + " | Position: " + record.getFinishPosition() + " | WPM: " + String.format("%.1f", record.getWpm()) + " | Accuracy: " + String.format("%.1f", record.getAccuracyPercentage()) + "%" + " | Burnouts: " + record.getBurnoutCount() + " | Accuracy Change: " + String.format("%+.2f", record.getAccuracyChange()) + " | Points: " + record.getPointsEarned() + "\n";
                    }

                    area.setText(text);
                    area.setBorder(BorderFactory.createTitledBorder(typists[i].getName()));

                    historyPanel.add(area);
                    historyPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        // makes the history scrollable so it doesnt overflow
        JScrollPane scrollPane = new JScrollPane(historyPanel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        return outerPanel;
    }

    /**
     * builds the comparison tab where typists can be compared.
     * the dropdown changes what stat is shown on the graph.
     *
     * @return panel containing the comparison graph
     */
    private JPanel buildComparisonPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JComboBox<String> metricBox = new JComboBox<>(new String[]{"WPM", "Accuracy", "Burnouts", "Finish Position", "Accuracy Change", "Personal Best WPM"});

        controls.add(new JLabel("Compare by:"));
        controls.add(metricBox);

        ComparisonGraphPanel graphPanel = new ComparisonGraphPanel(race, "WPM");

        // updates the graph when a different stat is picked
        metricBox.addActionListener(e -> {
            graphPanel.setMetric((String) metricBox.getSelectedItem());
            graphPanel.repaint();
        });

        panel.add(controls, BorderLayout.NORTH);
        panel.add(graphPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * builds the leaderboard tab showing ranks, points and titles.
     * also adds a small guide explaining what each title means.
     *
     * @return panel containing the leaderboard
     */
    private JPanel buildLeaderboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        RaceStats stats = race.getRaceStats();

        if (stats == null) {
            panel.add(new JLabel("No leaderboard data available."), BorderLayout.CENTER);
            return panel;
        }

        String[] columns = {"Rank", "Typist", "Points", "Personal Best WPM", "Title / Badge"};

        ArrayList<RawData> leaderboard = stats.getLeaderboard();

        String[][] data = new String[leaderboard.size()][columns.length];

        // fills the table with leaderboard data
        for (int i = 0; i < leaderboard.size(); i++) {
            RawData rawData = leaderboard.get(i);

            data[i][0] = String.valueOf(i + 1);
            data[i][1] = rawData.getTypistName();
            data[i][2] = String.valueOf(rawData.getCumulativePoints());
            data[i][3] = String.format("%.1f", rawData.getPersonalBestWpm());
            data[i][4] = rawData.getTitle();
        }

        JTable table = new JTable(data, columns);

        // makes table not editable
        table.setEnabled(false);

        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buildTitleGuidePanel(), BorderLayout.SOUTH);

        return panel;
    }


    /**
     * builds a small panel that explains what each title means.
     * shown under the leaderboard so users know how to get them.
     *
     * @return panel containing the title guide
     */
    private JPanel buildTitleGuidePanel() {
        JPanel guidePanel = new JPanel();
        guidePanel.setLayout(new BoxLayout(guidePanel, BoxLayout.Y_AXIS));
        guidePanel.setBorder(BorderFactory.createTitledBorder("Title Guide"));

        // lists each title and how its unlocked
        guidePanel.add(new JLabel("Speed Demon: 3+ consecutive wins"));
        guidePanel.add(new JLabel("Iron Fingers: 5 races without burnout"));
        guidePanel.add(new JLabel("Keyboard Legend: 70+ personal best WPM"));
        guidePanel.add(new JLabel("Race Champion: 25+ total points"));
        guidePanel.add(new JLabel("Rookie Racer: default title"));

        return guidePanel;
    }

    /**
     * builds the bottom section with navigation buttons.
     * currently just includes a button to go back to the race screen.
     *
     * @return panel containing the buttons
     */
    private JPanel buildButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton backButton = new JButton("Back to Race");

        // goes back to the race view when clicked
        backButton.addActionListener(e -> parentFrame.showRace());

        buttonPanel.add(backButton);

        return buttonPanel;
    }

    public JPanel getPanel() {
        return mainPanel;
    }
}