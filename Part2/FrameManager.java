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
    private boolean stopNextTick = false;

    private TypistConfigPanel typistConfigPanel;
    private StatsPanel statsPanel;
    private TypistConfig[] typistConfigs;
    private JButton statsButton;

    public FrameManager(Game game)
    {
        race = game;

        // set up frame for all panels to be within
        frame = new JFrame("Typing Race GAME");
        frame.setSize(1500, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // set up menu bar (proper Swing structure)
        buildMenuBar();

        // set up switching between panels -> create main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // create menu panel, typist config panel and race panel
        MenuPanel menuPanel = new MenuPanel(this);
        typistConfigPanel = new TypistConfigPanel(this);
        racePanel = new RacePanel(race);

        // set up the cardlayout switcher
        mainPanel.add(menuPanel.getPanel(), "MENU");
        mainPanel.add(buildRaceScreen(), "RACE");

        // add the panels to frame
        frame.setContentPane(mainPanel);

        // game loop controls
        timer = new Timer(200, e -> {
            race.advanceOneTurn();
            racePanel.repaintRace();

            // stops on the next fframe so can see who won
            if (stopNextTick)
            {
                timer.stop();
                stopNextTick = false;
                return;
            }

            if (race.isFinished())
            {
                stopNextTick = true;
                statsButton.setVisible(true);

                if (race.getWinner() != null)
                {
                    statusLabel.setText("Winner: " + race.getWinner().getName());
                }
                else
                {
                    statusLabel.setText("Race finished.");
                }
            }
            else
            {
                statusLabel.setText("Race in progress...");
            }
        });

        cardLayout.show(mainPanel, "MENU");
    }

    /**
     * Builds the menu bar.
     */
    private void buildMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");

        JMenuItem setupItem = new JMenuItem("Back to Setup");
        JMenuItem exitItem = new JMenuItem("Exit");

        // action if setup selected
        setupItem.addActionListener(e -> {
            if (timer != null)
            {
                timer.stop();
            }
            cardLayout.show(mainPanel, "MENU");
        });

        // action if exit selected
        exitItem.addActionListener(e -> frame.dispose());

        gameMenu.add(setupItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);

        frame.setJMenuBar(menuBar);
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
        statsButton = new JButton("Stats");
        statsButton.setVisible(false);

        // action if start clicked
        startButton.addActionListener(e -> {
            if (!race.isFinished())
            {
                stopNextTick = false;
                timer.start();
                statusLabel.setText("Race in progress...");
            }
        });

        // action if pause clicked
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
                    statsButton.setVisible(true);

                    if (race.getWinner() != null)
                    {
                        statusLabel.setText("Winner: " + race.getWinner().getName());
                    }
                    else
                    {
                        statusLabel.setText("Race finished.");
                    }
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
            stopNextTick = false;
            statsButton.setVisible(false);
            race.resetRace(typistConfigs);
            racePanel.repaintRace();
            statusLabel.setText("Race reset.");
        });

        // action if setup is clicked
        setupButton.addActionListener(e -> {
            timer.stop();
            cardLayout.show(mainPanel, "MENU");
        });

        //action if stats is clicked
        statsButton.addActionListener(e -> {
            showStats();
        });

        // add buttons to the button specific panel
        buttons.add(startButton);
        buttons.add(pauseButton);
        buttons.add(stepButton);
        buttons.add(resetButton);
        buttons.add(setupButton);
        buttons.add(statsButton);

        statusLabel = new JLabel("Ready.");

        buttonFooter.add(buttons, BorderLayout.NORTH);
        buttonFooter.add(statusLabel, BorderLayout.SOUTH);

        return buttonFooter;
    }

    /**
     * Shows the GUI window.
     */
    public void show()
    {
        frame.setVisible(true);
    }

    /**
     * Opens the typist configuration screen.
     *
     * @param config the selected game config
     */
    public void openTypistConfig(GameConfig config)
    {
        typistConfigPanel.buildPanel(config);

        mainPanel.add(typistConfigPanel.getPanel(), "TYPECONFIG");
        cardLayout.show(mainPanel, "TYPECONFIG");
    }

    /**
     * Shows the menu screen.
     */
    public void showMenu()
    {
        cardLayout.show(mainPanel, "MENU");
    }

    /**
     * Shows the race screen.
     */
    public void showRace()
    {
        cardLayout.show(mainPanel, "RACE");
    }

    /**
     * Shows the stats screen.
     */
    public void showStats()
    {
        statsPanel = new StatsPanel(this, race);
        statsPanel.buildPanel();

        mainPanel.add(statsPanel.getPanel(), "STATS");
        cardLayout.show(mainPanel, "STATS");
    }

    /**
     * Starts the race with the configured passage and typists.
     *
     * @param config the selected game config
     */
    public void startConfiguredRace(GameConfig config, TypistConfig[] typistConfigs)
    {
        this.typistConfigs = typistConfigs;
        // race.resetRace(typistConfigs); -> called in start race
        statsButton.setVisible(false);
        stopNextTick = false;
        race.StartRace(config, typistConfigs);
        racePanel.repaintRace();
        statusLabel.setText("Ready.");
        cardLayout.show(mainPanel, "RACE");
    }
}