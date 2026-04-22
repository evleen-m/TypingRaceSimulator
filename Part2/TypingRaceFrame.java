import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * The main GUI window for the Typing Race application.
 *
 * This class is responsible for:
 * - Creating and displaying the main window
 * - Handling user controls (Start, Pause, Step, Reset)
 * - Updating the race using a Swing Timer
 * - Displaying race status messages
 *
 * It interacts with the TypingRace class (model) and RacePanel (view).
 *
 * @author Evleen Manghera
 * @version 1
 */
public class TypingRaceFrame extends JFrame
{
    private TypingRace race;
    private RacePanel racePanel;  // panel used to draw the race
    private Timer timer;          // refreshes animation (runs every 200ms)
    private JLabel statusLabel;   // displays current race status

    /**
     * Constructs the GUI window and sets up all components.
     *
     * @param race the TypingRace instance to visualise
     */
    public TypingRaceFrame(TypingRace race)
    {
        this.race = race;
        this.racePanel = new RacePanel(race);

        // initialise window properties
        setTitle("Typing Race");
        setSize(820, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centre window
        setLayout(new BorderLayout());

        // add race visual panel (centre)
        add(racePanel, BorderLayout.CENTER);

        // add control buttons and status label (bottom)
        add(buildControls(), BorderLayout.SOUTH);

        /**
         * Timer runs every 200ms to simulate the race.
         * Each tick:
         * - advances the race by one turn
         * - repaints the panel
         * - checks if the race has finished
         */
        timer = new Timer(200, e -> {
            race.advanceOneTurn();
            racePanel.repaint();

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
    }

    /**
     * Builds the control panel containing buttons and status label.
     *
     * @return a JPanel containing all controls
     */
    private JPanel buildControls()
    {
        JPanel outer = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout());

        // create buttons
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton stepButton = new JButton("Step");
        JButton resetButton = new JButton("Reset");

        /**
         * Starts the race animation.
         */
        startButton.addActionListener(e -> {
            if (!race.isFinished())
            {
                timer.start();
                statusLabel.setText("Race in progress...");
            }
        });

        /**
         * Pauses the race animation.
         */
        pauseButton.addActionListener(e -> {
            timer.stop();
            statusLabel.setText("Paused.");
        });

        /**
         * Advances the race by one turn manually.
         */
        stepButton.addActionListener(e -> {
            if (!race.isFinished())
            {
                race.advanceOneTurn();
                racePanel.repaint();

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

        /**
         * Resets the race to the starting state.
         */
        resetButton.addActionListener(e -> {
            timer.stop();
            race.resetRace();
            racePanel.repaint();
            statusLabel.setText("Race reset.");
        });

        // add buttons to panel
        buttons.add(startButton);
        buttons.add(pauseButton);
        buttons.add(stepButton);
        buttons.add(resetButton);

        // label at bottom
        statusLabel = new JLabel("Ready.");

        outer.add(buttons, BorderLayout.NORTH);
        outer.add(statusLabel, BorderLayout.SOUTH);

        return outer;
    }
}