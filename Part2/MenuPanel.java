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

    private JCheckBox autocorrectOnBox;
    private JCheckBox coffeeModeOnBox;
    private JCheckBox nightShiftOnBox;

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

        JPanel centre = new JPanel(new GridLayout(5, 1, 10, 10));

        // passage choice box + custom length
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

        // shoice number of players box
        JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerPanel.add(new JLabel("Number of players:"));

        playerNumBox = new JComboBox<>(new Integer[] { 2, 3, 4, 5, 6 });
        playerPanel.add(playerNumBox);

        centre.add(playerPanel);

        // add difficulty modifiers
        JPanel modifierPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modifierPanel.add(new JLabel("Autocorrect On:"));
        autocorrectOnBox = new JCheckBox();
        modifierPanel.add(autocorrectOnBox);

        modifierPanel.add(new JLabel("Coffee Mode:"));
        coffeeModeOnBox = new JCheckBox();
        modifierPanel.add(coffeeModeOnBox);

        modifierPanel.add(new JLabel("Night Mode:"));
        nightShiftOnBox = new JCheckBox();
        modifierPanel.add(nightShiftOnBox);

        centre.add(modifierPanel);

        // create race button
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
        String passage = getPassageString();

        Integer playerNumber = (Integer) playerNumBox.getSelectedItem();
        if (playerNumber == null){
            throw new RuntimeException("Invalid player number");
        }

        boolean autocorrectOnBoxSelected = autocorrectOnBox.isSelected();
        boolean coffeeModeOnBoxSelected = coffeeModeOnBox.isSelected();
        boolean nightShiftOnBoxSelected = nightShiftOnBox.isSelected();


        GameConfig config = new GameConfig();
        config.autocorrectOn = autocorrectOnBoxSelected;
        config.coffeeModeOn = coffeeModeOnBoxSelected;
        config.nightShiftOn = nightShiftOnBoxSelected;
        config.playerNumber = playerNumber;
        config.passageText = passage;


        //parentFrame.startConfiguredRace(passage, playerNumber, config);
        parentFrame.openTypistConfig(config);
    }


    private String getPassageString() {
        String selected = (String) passageChoiceBox.getSelectedItem();
        String passage = shortPassage;

        // check passage length
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
        return passage;
    }


}