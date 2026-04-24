import javax.swing.*;
import java.awt.*;

/**
 * Builds and manages the typist configuration screen.
 * Uses composition instead of extending JPanel.
 */
public class TypistConfigPanel
{
    private FrameManager parentFrame;
    private JPanel mainPanel;

    private JComboBox<String>[] typingConfigBoxes;

    private GameConfig config;

    public TypistConfigPanel(FrameManager parentFrame)
    {
        this.parentFrame = parentFrame;
    }

    /**
     * Builds the typist configuration panel.
     *
     * @param config the selected game config
     */
    public void buildPanel(GameConfig config)
    {
        this.config = config;

        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Configure Typists");
        title.setFont(title.getFont().deriveFont(24f));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(config.playerNumber, 1, 10, 10));

        typingConfigBoxes = new JComboBox[config.playerNumber];

        for (int i = 0; i < config.playerNumber; i++)
        {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

            row.add(new JLabel("Typist " + (i + 1) + " config:"));

            typingConfigBoxes[i] = new JComboBox<>(new String[] {
                    "Touch Typist",
                    "Hunt & Peck",
                    "Phone Thumbs",
                    "Voice-to-Text"
            });

            row.add(typingConfigBoxes[i]);

            centre.add(row);
        }

        mainPanel.add(centre, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Race");
        startButton.addActionListener(e -> startRace());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> parentFrame.showMenu());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(startButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Returns the built typist configuration panel.
     *
     * @return the typist configuration panel
     */
    public JPanel getPanel()
    {
        return mainPanel;
    }

    /**
     * Starts the race using the configured typists.
     */
    private void startRace()
    {
        TypistConfig[] typistConfigs = new TypistConfig[config.playerNumber];

        for (int i = 0; i < config.playerNumber; i++)
        {
            String selectedConfig = (String) typingConfigBoxes[i].getSelectedItem();

            TypistConfig typistConfig = new TypistConfig();
            typistConfig.setTypingStyle(selectedConfig);

            typistConfigs[i] = typistConfig;
        }

        parentFrame.startConfiguredRace(config, typistConfigs);
    }
}