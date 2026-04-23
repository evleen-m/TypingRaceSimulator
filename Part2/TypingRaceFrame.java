import javax.swing.*;
import java.awt.*;

/**
 * GUI controller for the typing race.
 * Uses composition instead of extending JFrame.
 */
public class TypingRaceFrame
{
    private JFrame frame;
    private TypingRace race;
    private RacePanel racePanel;
    private Timer timer;
    private JLabel statusLabel;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public TypingRaceFrame(TypingRace race)
    {
        this.race = race;

        frame = new JFrame("Typing Race");
        frame.setSize(1500, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        SetupPanel setupPanel = new SetupPanel(this);
        racePanel = new RacePanel(race);

        mainPanel.add(setupPanel.getPanel(), "SETUP");
        mainPanel.add(buildRaceScreen(), "RACE");

        frame.add(mainPanel);

        timer = new Timer(200, e -> {
            race.advanceOneTurn();
            racePanel.repaintRace();

            if (race.isFinished())
            {
                timer.stop();
                statusLabel.setText("Winner: " + race.getWinner().getName());
            }
            else
            {
                statusLabel.setText("Race in progress...");
            }
        });

        cardLayout.show(mainPanel, "SETUP");
    }

    /**
     * Builds the race screen panel.
     *
     * @return the race screen panel
     */
    private JPanel buildRaceScreen()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(racePanel.getPanel(), BorderLayout.CENTER);
        panel.add(buildControls(), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Builds the control panel.
     *
     * @return the control panel
     */
    private JPanel buildControls()
    {
        JPanel outer = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout());

        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton stepButton = new JButton("Step");
        JButton resetButton = new JButton("Reset");
        JButton setupButton = new JButton("Back to Setup");

        startButton.addActionListener(e -> {
            if (!race.isFinished())
            {
                timer.start();
                statusLabel.setText("Race in progress...");
            }
        });

        pauseButton.addActionListener(e -> {
            timer.stop();
            statusLabel.setText("Paused.");
        });

        stepButton.addActionListener(e -> {
            if (!race.isFinished())
            {
                race.advanceOneTurn();
                racePanel.repaintRace();

                if (race.isFinished())
                {
                    statusLabel.setText("Winner: " + race.getWinner().getName());
                }
                else
                {
                    statusLabel.setText("Advanced one turn.");
                }
            }
        });

        resetButton.addActionListener(e -> {
            timer.stop();
            race.resetRace();
            racePanel.repaintRace();
            statusLabel.setText("Race reset.");
        });

        setupButton.addActionListener(e -> {
            timer.stop();
            cardLayout.show(mainPanel, "SETUP");
        });

        buttons.add(startButton);
        buttons.add(pauseButton);
        buttons.add(stepButton);
        buttons.add(resetButton);
        buttons.add(setupButton);

        statusLabel = new JLabel("Ready.");

        outer.add(buttons, BorderLayout.NORTH);
        outer.add(statusLabel, BorderLayout.SOUTH);

        return outer;
    }

    /**
     * Starts the race with the configured passage.
     *
     * @param passageText the selected passage text
     */
    public void startConfiguredRace(String passageText)
    {
        race.setPassageText(passageText);
        race.resetRace();
        racePanel.repaintRace();
        statusLabel.setText("Ready.");
        cardLayout.show(mainPanel, "RACE");
    }

    /**
     * Shows the GUI window.
     */
    public void show()
    {
        frame.setVisible(true);
    }
}