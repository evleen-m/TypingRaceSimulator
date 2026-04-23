import javax.swing.*;
import java.awt.*;

/**
 * Builds and manages the setup screen for the race.
 * Uses composition instead of extending JPanel.
 */
public class MenuPanel
{
    private FrameManager parentFrame;

    private JPanel mainPanel;
    private JComboBox<String> passageChoiceBox;
    private JTextArea customPassageArea;

    private static final String shortPassage =
            "The quick brown fox jumps over the lazy dog.";

    private static final String medPassage =
            "Typing races reward speed, accuracy, and consistency over time. Every character typed correctly moves the player closer to victory.";

    private static final String longPassage =
            "Graphical user interfaces allow programs to feel more interactive and polished. In this typing race, each typist advances through the actual passage text, making progress easier to follow and the simulation more engaging for the user.";

    private JComboBox<Integer> playerNumBox;

    public MenuPanel(FrameManager parentFrame)
    {
        this.parentFrame = parentFrame;
        buildPanel();
    }

    /**
     * Builds the setup panel.
     */
    private void buildPanel()
    {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Set up the race");
        title.setFont(title.getFont().deriveFont(24f));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        presetPanel.add(new JLabel("Choose passage:"));

        passageChoiceBox = new JComboBox<>(new String[] {
                "Short", "Medium", "Long", "Custom"
        });

        presetPanel.add(passageChoiceBox);
        centre.add(presetPanel);

        JPanel customPanel = new JPanel(new BorderLayout());
        customPanel.add(new JLabel("Custom passage:"), BorderLayout.NORTH);

        customPassageArea = new JTextArea(5, 40);
        customPassageArea.setLineWrap(true);
        customPassageArea.setWrapStyleWord(true);

        customPanel.add(new JScrollPane(customPassageArea), BorderLayout.CENTER);
        centre.add(customPanel);

        playerNumBox = new JComboBox<>(new Integer[] { 2, 3, 4, 5, 6 });
        presetPanel.add(playerNumBox);

        JButton startButton = new JButton("Create Race");
        startButton.addActionListener(e -> createRace());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(startButton);
        centre.add(buttonPanel);

        mainPanel.add(centre, BorderLayout.CENTER);
    }

    /**
     * Returns the built setup panel.
     *
     * @return the setup panel
     */
    public JPanel getPanel()
    {
        return mainPanel;
    }

    /**
     * Creates the race based on the selected passage.
     */
    private void createRace()
    {
        String selected = (String) passageChoiceBox.getSelectedItem();
        String passage = shortPassage;

        if ("Medium".equals(selected))
        {
            passage = medPassage;
        }
        else if ("Long".equals(selected))
        {
            passage = longPassage;
        }
        else if ("Custom".equals(selected))
        {
            String custom = customPassageArea.getText().trim();
            if (!custom.isEmpty())
            {
                passage = custom;
            }
        }

        Integer playerNumber = (Integer) playerNumBox.getSelectedItem();

        if (playerNumber == null){
            throw new RuntimeException("Invalid player number");
        }

        parentFrame.startConfiguredRace(passage, playerNumber);
    }
}