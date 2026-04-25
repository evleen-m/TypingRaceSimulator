import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Displays race statistics for each typist.
 * Uses tabs to separate current race stats, history, and comparison views.
 */
public class StatsPanel
{
    //
    private FrameManager parentFrame;
    private Game race;
    private JPanel mainPanel;

    public StatsPanel(FrameManager parentFrame, Game race)
    {
        this.parentFrame = parentFrame;
        this.race = race;
    }

    /**
     * Builds the full stats panel.
     */
    public void buildPanel()
    {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Typist Statistics");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(buildStatsSections(), BorderLayout.CENTER);
        mainPanel.add(buildButtons(), BorderLayout.SOUTH);
    }

    /**
     * Builds the main tabbed stats area.
     *
     * @return the stats tabs panel
     */
    private JPanel buildStatsSections()
    {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Current Race", buildCurrentRacePanel());
        tabs.addTab("History", buildHistoryPanel());
        tabs.addTab("Compare", buildComparisonPanel());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds the current race tab.
     *
     * @return the current race panel
     */
    private JPanel buildCurrentRacePanel()
    {
        JPanel panel = new JPanel(new BorderLayout(15, 15));

        panel.add(buildWpmList(), BorderLayout.WEST);
        panel.add(new WpmLineGraphPanel(race), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds the left side stats list for the current race.
     *
     * @return the WPM and stats list
     */
    private JPanel buildWpmList()
    {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setPreferredSize(new Dimension(330, 400));

        listPanel.setBorder(BorderFactory.createTitledBorder("Current Race Stats"));

        Typist[] typists = race.getSeats();
        RaceStats stats = race.getRaceStats();

        if (typists != null)
        {
            for (int i = 0; i < typists.length; i++)
            {
                if (typists[i] != null)
                {
                    double wpm = race.calculateWPM(typists[i]);

                    JLabel nameLabel = new JLabel(
                            typists[i].getName() + ": " + String.format("%.1f", wpm) + " WPM"
                    );

                    JLabel positionLabel = new JLabel(
                            "Finish Position: " + typists[i].getFinishPosition()
                    );

                    JLabel accuracyLabel = new JLabel(
                            "Accuracy: " + String.format("%.1f", typists[i].getAccuracyPercentage()) + "%"
                    );

                    JLabel burnoutLabel = new JLabel(
                            "Burnouts: " + typists[i].getBurnoutCount()
                    );

                    JLabel burnoutTurnsLabel = new JLabel(
                            "Burnout Turns: " + typists[i].getBurnoutTurns()
                    );

                    JLabel accuracyChangeLabel = new JLabel(
                            "Accuracy Change: " + String.format("%+.2f", typists[i].getAccuracyChange())
                    );

                    JLabel personalBestLabel = new JLabel("Personal Best WPM: N/A");

                    if (stats != null)
                    {
                        personalBestLabel.setText(
                                "Personal Best WPM: " +
                                        String.format("%.1f", stats.getPersonalBestWpm(typists[i].getName()))
                        );
                    }

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

                    listPanel.add(nameLabel);
                    listPanel.add(positionLabel);
                    listPanel.add(accuracyLabel);
                    listPanel.add(burnoutLabel);
                    listPanel.add(burnoutTurnsLabel);
                    listPanel.add(accuracyChangeLabel);
                    listPanel.add(personalBestLabel);
                    listPanel.add(Box.createVerticalStrut(15));
                }
            }
        }

        return listPanel;
    }

    /**
     * Builds the historical race data tab.
     *
     * @return the history panel
     */
    private JPanel buildHistoryPanel()
    {
        JPanel outerPanel = new JPanel(new BorderLayout());

        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Typist[] typists = race.getSeats();
        RaceStats stats = race.getRaceStats();

        if (typists == null || stats == null)
        {
            historyPanel.add(new JLabel("No history available."));
        }
        else
        {
            for (int i = 0; i < typists.length; i++)
            {
                if (typists[i] != null)
                {
                    JTextArea area = new JTextArea();
                    area.setEditable(false);
                    area.setFont(new Font("Monospaced", Font.PLAIN, 13));

                    String text = typists[i].getName() + "\n";
                    text += "Personal Best WPM: " +
                            String.format("%.1f", stats.getPersonalBestWpm(typists[i].getName())) + "\n\n";

                    ArrayList<RaceRecord> history = stats.getRaceHistory(typists[i].getName());

                    if (history.isEmpty())
                    {
                        text += "No completed races recorded yet.\n";
                    }

                    for (int j = 0; j < history.size(); j++)
                    {
                        RaceRecord record = history.get(j);

                        text += "Race " + record.getRaceNumber()
                                + " | Position: " + record.getFinishPosition()
                                + " | WPM: " + String.format("%.1f", record.getWpm())
                                + " | Accuracy: " + String.format("%.1f", record.getAccuracyPercentage()) + "%"
                                + " | Burnouts: " + record.getBurnoutCount()
                                + " | Accuracy Change: " + String.format("%+.2f", record.getAccuracyChange())
                                + "\n";
                    }

                    area.setText(text);
                    area.setBorder(BorderFactory.createTitledBorder(typists[i].getName()));

                    historyPanel.add(area);
                    historyPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(historyPanel);
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        return outerPanel;
    }

    /**
     * Builds the comparison tab.
     *
     * @return the comparison panel
     */
    private JPanel buildComparisonPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JComboBox<String> metricBox = new JComboBox<>(new String[] {
                "WPM",
                "Accuracy",
                "Burnouts",
                "Finish Position",
                "Accuracy Change",
                "Personal Best WPM"
        });

        controls.add(new JLabel("Compare by:"));
        controls.add(metricBox);

        ComparisonGraphPanel graphPanel = new ComparisonGraphPanel(race, "WPM");

        metricBox.addActionListener(e -> {
            graphPanel.setMetric((String) metricBox.getSelectedItem());
            graphPanel.repaint();
        });

        panel.add(controls, BorderLayout.NORTH);
        panel.add(graphPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds the bottom button area.
     *
     * @return the button panel
     */
    private JPanel buildButtons()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton backButton = new JButton("Back to Race");
        backButton.addActionListener(e -> parentFrame.showRace());

        buttonPanel.add(backButton);

        return buttonPanel;
    }

    /**
     * Returns the built stats panel.
     *
     * @return the stats panel
     */
    public JPanel getPanel()
    {
        return mainPanel;
    }
}