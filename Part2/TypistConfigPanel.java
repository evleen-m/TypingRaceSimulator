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

    private JComboBox<String>[] typingStyleBoxes;
    private JComboBox<String>[] keyboardTypeBoxes;

    private JCheckBox[] wristSupportBoxes;
    private JCheckBox[] energyDrinkBoxes;
    private JCheckBox[] headphonesBoxes;

    private JButton[] colourButtons;
    private Color[] selectedColours;
    private JComboBox<String>[] symbolBoxes;

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

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

        typingStyleBoxes = new JComboBox[config.playerNumber];
        keyboardTypeBoxes = new JComboBox[config.playerNumber];
        symbolBoxes = new JComboBox[config.playerNumber];

        wristSupportBoxes = new JCheckBox[config.playerNumber];
        energyDrinkBoxes = new JCheckBox[config.playerNumber];
        headphonesBoxes = new JCheckBox[config.playerNumber];

        colourButtons = new JButton[config.playerNumber];
        selectedColours = new Color[config.playerNumber];

        for (int i = 0; i < config.playerNumber; i++)
        {
            centre.add(buildTypistRow(i));
            centre.add(Box.createVerticalStrut(10));
        }

        mainPanel.add(new JScrollPane(centre), BorderLayout.CENTER);

        JButton startButton = new JButton("Start Race");
        startButton.addActionListener(e -> startRace());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> parentFrame.showMenu());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(startButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel buildTypistRow(int index)
    {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        row.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.anchor = GridBagConstraints.WEST;

        JLabel typistTitle = new JLabel("Typist " + (index + 1));
        typistTitle.setFont(typistTitle.getFont().deriveFont(Font.BOLD, 16f));

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        row.add(typistTitle, c);

        c.gridwidth = 1;

        c.gridx = 0;
        c.gridy = 1;
        row.add(new JLabel("Typing Style:"), c);

        typingStyleBoxes[index] = new JComboBox<>(new String[] {
                "Touch Typist",
                "Hunt & Peck",
                "Phone Thumbs",
                "Voice-to-Text"
        });

        c.gridx = 1;
        row.add(typingStyleBoxes[index], c);

        c.gridx = 2;
        row.add(new JLabel("Keyboard:"), c);

        keyboardTypeBoxes[index] = new JComboBox<>(new String[] {
                "Mechanical",
                "Membrane",
                "Touchscreen",
                "Stenography"
        });

        c.gridx = 3;
        row.add(keyboardTypeBoxes[index], c);

        symbolBoxes[index] = new JComboBox<>(new String[] {
                "Ⓐ", "Ⓑ", "Ⓒ", "★", "●", "◆", "■", "▲", "⌨", "⚡"
        });

        c.gridx = 2;
        c.gridy = 3;
        row.add(new JLabel("Symbol:"), c);

        c.gridx = 3;
        row.add(symbolBoxes[index], c);

        wristSupportBoxes[index] = new JCheckBox("Wrist Support");
        energyDrinkBoxes[index] = new JCheckBox("Energy Drink");
        headphonesBoxes[index] = new JCheckBox("Noise-Cancelling Headphones");

        wristSupportBoxes[index].setBackground(Color.WHITE);
        energyDrinkBoxes[index].setBackground(Color.WHITE);
        headphonesBoxes[index].setBackground(Color.WHITE);

        c.gridx = 0;
        c.gridy = 2;
        row.add(new JLabel("Accessories:"), c);

        c.gridx = 1;
        row.add(wristSupportBoxes[index], c);

        c.gridx = 2;
        row.add(energyDrinkBoxes[index], c);

        c.gridx = 3;
        row.add(headphonesBoxes[index], c);

        selectedColours[index] = Color.BLUE;

        colourButtons[index] = new JButton("Choose Colour");
        colourButtons[index].setBackground(selectedColours[index]);
        colourButtons[index].setOpaque(true);
        colourButtons[index].setPreferredSize(new Dimension(150, 30));

        int buttonIndex = index;

        colourButtons[index].addActionListener(e -> {
            Color chosenColour = JColorChooser.showDialog(
                    mainPanel,
                    "Choose Typist Colour",
                    selectedColours[buttonIndex]
            );

            if (chosenColour != null)
            {
                selectedColours[buttonIndex] = chosenColour;
                colourButtons[buttonIndex].setBackground(chosenColour);
                colourButtons[buttonIndex].setText("Colour Selected");
            }
        });

        c.gridx = 0;
        c.gridy = 3;
        row.add(new JLabel("Colour:"), c);

        c.gridx = 1;
        row.add(colourButtons[index], c);

        JLabel impactLabel = new JLabel("Style affects accuracy/burnout. Keyboard affects mistypes. Accessories add bonuses.");
        impactLabel.setFont(impactLabel.getFont().deriveFont(12f));
        impactLabel.setForeground(new Color(90, 90, 90));

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        row.add(impactLabel, c);

        return row;
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
            TypistConfig typistConfig = new TypistConfig();

            typistConfig.setTypingStyle((String) typingStyleBoxes[i].getSelectedItem());
            typistConfig.setKeyboardType((String) keyboardTypeBoxes[i].getSelectedItem());

            typistConfig.setWristSupport(wristSupportBoxes[i].isSelected());
            typistConfig.setEnergyDrink(energyDrinkBoxes[i].isSelected());
            typistConfig.setHeadphones(headphonesBoxes[i].isSelected());

            typistConfig.setColour(selectedColours[i]);
            typistConfig.setSymbol((String) symbolBoxes[i].getSelectedItem());

            typistConfigs[i] = typistConfig;
        }

        parentFrame.startConfiguredRace(config, typistConfigs);
    }
}