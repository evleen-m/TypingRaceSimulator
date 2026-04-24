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

        // main container panel
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // title at top
        JLabel title = new JLabel("Configure Typists");
        title.setFont(title.getFont().deriveFont(24f));
        mainPanel.add(title, BorderLayout.NORTH);

        // centre panel now split into 2 sections (left = config, right = info)
        JPanel centre = new JPanel(new BorderLayout(15, 15));

        // left side where all typist config rows go
        JPanel configSection = new JPanel();
        configSection.setLayout(new BoxLayout(configSection, BoxLayout.Y_AXIS));

        // initilise arrays for all inputs
        typingStyleBoxes = new JComboBox[config.playerNumber];
        keyboardTypeBoxes = new JComboBox[config.playerNumber];
        symbolBoxes = new JComboBox[config.playerNumber];

        wristSupportBoxes = new JCheckBox[config.playerNumber];
        energyDrinkBoxes = new JCheckBox[config.playerNumber];
        headphonesBoxes = new JCheckBox[config.playerNumber];

        colourButtons = new JButton[config.playerNumber];
        selectedColours = new Color[config.playerNumber];

        // build each typist row
        for (int i = 0; i < config.playerNumber; i++)
        {
            configSection.add(buildTypistRow(i));
            configSection.add(Box.createVerticalStrut(10)); // spacing betwen rows
        }

        // scroll incase lots of typists
        JScrollPane configScroll = new JScrollPane(configSection);
        configScroll.setPreferredSize(new Dimension(900, 600));

        // add both sections to centre
        centre.add(configScroll, BorderLayout.CENTER); // left side
        centre.add(buildInfoPanel(), BorderLayout.EAST); // right side info

        mainPanel.add(centre, BorderLayout.CENTER);

        // bottom buttons
        JButton startButton = new JButton("Start Race");
        startButton.addActionListener(e -> startRace());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> parentFrame.showMenu());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(startButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // builds one typist row with all config options
    private JPanel buildTypistRow(int index)
    {
        // main row panel using gridbag for layout control
        JPanel row = new JPanel(new GridBagLayout());

        // add border and spacing around the row
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        row.setBackground(Color.WHITE);

        // layout constraints used for positioning elements
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8); // spacing betwen components
        c.anchor = GridBagConstraints.WEST; // align everything to left

        // typist title label (e.g. typist 1)
        JLabel typistTitle = new JLabel("Typist " + (index + 1));
        typistTitle.setFont(typistTitle.getFont().deriveFont(Font.BOLD, 16f));

        // place title at top spanning whole row
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        row.add(typistTitle, c);

        c.gridwidth = 1; // reset width

        // typing style label
        c.gridx = 0;
        c.gridy = 1;
        row.add(new JLabel("Typing Style:"), c);

        // dropdown for typing style
        typingStyleBoxes[index] = new JComboBox<>(new String[] {
                "Touch Typist",
                "Hunt & Peck",
                "Phone Thumbs",
                "Voice-to-Text"
        });

        c.gridx = 1;
        row.add(typingStyleBoxes[index], c);

        // keyboard label
        c.gridx = 2;
        row.add(new JLabel("Keyboard:"), c);

        // dropdown for keyboard type
        keyboardTypeBoxes[index] = new JComboBox<>(new String[] {
                "Mechanical",
                "Membrane",
                "Touchscreen",
                "Stenography"
        });

        c.gridx = 3;
        row.add(keyboardTypeBoxes[index], c);

        // symbol selection dropdown
        symbolBoxes[index] = new JComboBox<>(new String[] {
                "Ⓐ", "Ⓑ", "Ⓒ", "★", "●", "◆", "■", "▲", "⌨", "⚡"
        });

        // symbol label + dropdown placement
        c.gridx = 2;
        c.gridy = 3;
        row.add(new JLabel("Symbol:"), c);

        c.gridx = 3;
        row.add(symbolBoxes[index], c);

        // accessories checkboxes
        wristSupportBoxes[index] = new JCheckBox("Wrist Support");
        energyDrinkBoxes[index] = new JCheckBox("Energy Drink");
        headphonesBoxes[index] = new JCheckBox("Noise-Cancelling Headphones");

        // match background to row for clean look
        wristSupportBoxes[index].setBackground(Color.WHITE);
        energyDrinkBoxes[index].setBackground(Color.WHITE);
        headphonesBoxes[index].setBackground(Color.WHITE);

        // accessories label
        c.gridx = 0;
        c.gridy = 2;
        row.add(new JLabel("Accessories:"), c);

        // add each accessory checkbox
        c.gridx = 1;
        row.add(wristSupportBoxes[index], c);

        c.gridx = 2;
        row.add(energyDrinkBoxes[index], c);

        c.gridx = 3;
        row.add(headphonesBoxes[index], c);

        // default colour for typist
        selectedColours[index] = Color.BLUE;

        // button to pick colour
        colourButtons[index] = new JButton("Choose Colour");
        colourButtons[index].setBackground(selectedColours[index]);
        colourButtons[index].setOpaque(true);
        colourButtons[index].setPreferredSize(new Dimension(150, 30));

        int buttonIndex = index; // needed for lambda capture

        // open colour picker popup when clicked
        colourButtons[index].addActionListener(e -> {
            Color chosenColour = JColorChooser.showDialog(
                    mainPanel,
                    "Choose Typist Colour",
                    selectedColours[buttonIndex]
            );

            // update colour if user picked one
            if (chosenColour != null)
            {
                selectedColours[buttonIndex] = chosenColour;
                colourButtons[buttonIndex].setBackground(chosenColour);
                colourButtons[buttonIndex].setText("Colour Selected");
            }
        });

        // colour label + button placement
        c.gridx = 0;
        c.gridy = 3;
        row.add(new JLabel("Colour:"), c);

        c.gridx = 1;
        row.add(colourButtons[index], c);

        // small helper text explaining effects (not dynamic just info)
        JLabel impactLabel = new JLabel(
                "Typing Style affects accuracy/burnout.  Keyboard affects mistypes.  Accessories add bonuses."
        );

        impactLabel.setFont(impactLabel.getFont().deriveFont(12f));
        impactLabel.setForeground(new Color(90, 90, 90));

        // place info text across full row
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        row.add(impactLabel, c);

        return row;
    }

    // builds the info panel on the right side
    private JPanel buildInfoPanel()
    {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setPreferredSize(new Dimension(390, 600));
        infoPanel.setBackground(new Color(245, 247, 250));

        // border for seperation
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // heading
        JLabel heading = new JLabel("Modifier Information");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 20f));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(heading);
        infoPanel.add(Box.createVerticalStrut(15));

        // typing style info
        infoPanel.add(createInfoSection(
                "Typing Styles",
                "Touch Typist: Higher accuracy, lower burnout risk.\n" +
                        "Hunt & Peck: lower accuracy but safer.\n" +
                        "Phone Thumbs: slightly worse accuracy, more burnout.\n" +
                        "Voice-to-text: high accuracy but a bit unstable."
        ));

        infoPanel.add(Box.createVerticalStrut(12));

        // keyboard info
        infoPanel.add(createInfoSection(
                "Keyboard Types",
                "Mechanical: Faster typing, less mistakes.\n" +
                        "Membrane: normal balanced option.\n" +
                        "Touchscreen: slower and more mistakes.\n" +
                        "Stenography: Very fast typing speed."
        ));

        infoPanel.add(Box.createVerticalStrut(12));

        // accessories info
        infoPanel.add(createInfoSection(
                "Accessories",
                "Wrist Support: reduces burnout duration.\n" +
                        "Energy Drink: better early, worse later.\n" +
                        "Noise Cancelling Headphones: reduces mistakes."
        ));

        infoPanel.add(Box.createVerticalStrut(12));

        // symbol + colour info
        infoPanel.add(createInfoSection(
                "Symbol & Colour",
                "Symbol: what appears on the race track.\n" +
                        "Colour: changes typist icon and text highlight."
        ));

        return infoPanel;
    }

    // helper method to build each info section box
    private JPanel createInfoSection(String title, String text)
    {
        JPanel section = new JPanel(new BorderLayout(5, 5));
        section.setBackground(Color.WHITE);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // border around each info box
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.WHITE);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textArea.setForeground(new Color(70, 70, 70));

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(textArea, BorderLayout.CENTER);

        return section;
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