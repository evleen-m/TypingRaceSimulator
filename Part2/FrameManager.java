import javax.swing.*;
import java.awt.*;

/**
 * GUI controller for the typing race.
 * Uses composition instead of extending JFrame.
 */
public class FrameManager
{
    private JFrame frame;
    private Game race;
    private RacePanel racePanel;
    private Timer timer;
    private JLabel statusLabel;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public FrameManager(Game game)
    {
        race = game;
        //set up frame for all panels to be within
        frame = new JFrame("Typing Race GAME");
        frame.setSize(1500, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // set up switching between panels -> create main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // create menu panel and race panel
        MenuPanel menuPanel = new MenuPanel(this);
        racePanel = new RacePanel(race);

        // set up the cardlayout switcher
        mainPanel.add(menuPanel.getPanel(), "MENU");
        mainPanel.add(buildRaceScreen(), "RACE");

        // add the panels to frame
        frame.add(mainPanel);

        // game loop controls
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

        cardLayout.show(mainPanel, "MENU");
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
        // button container panels
        JPanel buttonFooter = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout());

        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton stepButton = new JButton("Step");
        JButton resetButton = new JButton("Reset");
        JButton setupButton = new JButton("Back to Setup");

        // ation if start clicked
        startButton.addActionListener(e -> {
            if (!race.isFinished())
            {
                timer.start();
                statusLabel.setText("Race in progress...");
            }
        });

        // action is paused clicked
        pauseButton.addActionListener(e -> {
            timer.stop();
            statusLabel.setText("Paused.");
        });

        // action if step is clicked
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

        // action if reset is clicked
        resetButton.addActionListener(e -> {
            timer.stop();
            race.resetRace();
            racePanel.repaintRace();
            statusLabel.setText("Race reset.");
        });

        // action if setup is clicked
        setupButton.addActionListener(e -> {
            timer.stop();
            cardLayout.show(mainPanel, "MENU");
        });

        // add buttons to the button specific panel
        buttons.add(startButton);
        buttons.add(pauseButton);
        buttons.add(stepButton);
        buttons.add(resetButton);
        buttons.add(setupButton);

        statusLabel = new JLabel("Ready.");

        buttonFooter.add(buttons, BorderLayout.NORTH);
        buttonFooter.add(statusLabel, BorderLayout.SOUTH);

        return buttonFooter;
    }

    /**
     * Starts the race with the configured passage.
     *
     * @param passageText the selected passage text
     */
    public void startConfiguredRace(String passageText, int playerNumber)
    {
        race.StartRace(passageText, playerNumber);
        //race.setPassageText(passageText);
        //race.resetRace();
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