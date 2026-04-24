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
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Set up the race");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(new Color(245, 247, 250));

        centre.add(buildPassageCard());
        centre.add(Box.createVerticalStrut(12));
        centre.add(buildPlayerCard());
        centre.add(Box.createVerticalStrut(12));
        centre.add(buildModifierCard());

        mainPanel.add(centre, BorderLayout.CENTER);

        JButton startButton = new JButton("Continue to Typist Setup");
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD, 14f));
        startButton.addActionListener(e -> createRace());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.add(startButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel buildPassageCard()
    {
        JPanel card = createCard();

        GridBagConstraints c = createConstraints();

        JLabel heading = createHeading("Passage Settings");

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        card.add(heading, c);

        c.gridwidth = 1;

        c.gridx = 0;
        c.gridy = 1;
        card.add(new JLabel("Choose passage:"), c);

        passageChoiceBox = new JComboBox<>(new String[] {
                "Short", "Medium", "Long", "Custom"
        });

        c.gridx = 1;
        card.add(passageChoiceBox, c);

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        card.add(new JLabel("Custom passage:"), c);

        customPassageArea = new JTextArea(5, 45);
        customPassageArea.setLineWrap(true);
        customPassageArea.setWrapStyleWord(true);

        JScrollPane customScroll = new JScrollPane(customPassageArea);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        card.add(customScroll, c);

        JLabel hint = new JLabel("Only used when Custom is selected.");
        hint.setFont(hint.getFont().deriveFont(12f));
        hint.setForeground(new Color(90, 90, 90));

        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        card.add(hint, c);

        return card;
    }

    private JPanel buildPlayerCard()
    {
        JPanel card = createCard();

        GridBagConstraints c = createConstraints();

        JLabel heading = createHeading("Race Settings");

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        card.add(heading, c);

        c.gridwidth = 1;

        c.gridx = 0;
        c.gridy = 1;
        card.add(new JLabel("Number of players:"), c);

        playerNumBox = new JComboBox<>(new Integer[] { 2, 3, 4, 5, 6 });

        c.gridx = 1;
        card.add(playerNumBox, c);

        JLabel hint = new JLabel("Each player will be configured on the next screen.");
        hint.setFont(hint.getFont().deriveFont(12f));
        hint.setForeground(new Color(90, 90, 90));

        c.gridx = 1;
        c.gridy = 2;
        card.add(hint, c);

        return card;
    }

    private JPanel buildModifierCard()
    {
        JPanel card = createCard();

        GridBagConstraints c = createConstraints();

        JLabel heading = createHeading("Global Difficulty Modifiers");

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        card.add(heading, c);

        c.gridwidth = 1;

        autocorrectOnBox = new JCheckBox("Autocorrect");
        coffeeModeOnBox = new JCheckBox("Caffeine Mode");
        nightShiftOnBox = new JCheckBox("Night Shift");

        styleCheckbox(autocorrectOnBox);
        styleCheckbox(coffeeModeOnBox);
        styleCheckbox(nightShiftOnBox);

        c.gridx = 0;
        c.gridy = 1;
        card.add(autocorrectOnBox, c);

        c.gridx = 1;
        card.add(coffeeModeOnBox, c);

        c.gridx = 2;
        card.add(nightShiftOnBox, c);

        JLabel autocorrectText = createSmallText("Halves slide-back after mistypes.");
        JLabel caffeineText = createSmallText("Early speed boost, later burnout risk.");
        JLabel nightText = createSmallText("Slightly lowers everyone’s accuracy.");

        c.gridy = 2;

        c.gridx = 0;
        card.add(autocorrectText, c);

        c.gridx = 1;
        card.add(caffeineText, c);

        c.gridx = 2;
        card.add(nightText, c);

        return card;
    }

    private JPanel createCard()
    {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private GridBagConstraints createConstraints()
    {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    private JLabel createHeading(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        return label;
    }

    private JLabel createSmallText(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(12f));
        label.setForeground(new Color(90, 90, 90));
        return label;
    }

    private void styleCheckbox(JCheckBox checkBox)
    {
        checkBox.setBackground(Color.WHITE);
        checkBox.setFocusPainted(false);
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

        if (playerNumber == null)
        {
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

        parentFrame.openTypistConfig(config);
    }

    private String getPassageString()
    {
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