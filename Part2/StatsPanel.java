import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Displays race statistics for each typist.
 * For now, only WPM is shown.
 */
public class StatsPanel
{
    private FrameManager parentFrame;
    private Game race;
    private JPanel mainPanel;

    public StatsPanel(FrameManager parentFrame, Game race)
    {
        this.parentFrame = parentFrame;
        this.race = race;
    }

    public void buildPanel()
    {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Typist Statistics");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(buildStatsSections(), BorderLayout.CENTER);
        mainPanel.add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildStatsSections()
    {
        JPanel panel = new JPanel(new BorderLayout(15, 15));

        panel.add(buildWpmList(), BorderLayout.WEST);
        panel.add(new WpmLineGraphPanel(race), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildWpmList()
    {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setPreferredSize(new Dimension(260, 400));

        listPanel.setBorder(BorderFactory.createTitledBorder("Final / Current WPM"));

        Typist[] typists = race.getSeats();

        if (typists != null)
        {
            for (int i = 0; i < typists.length; i++)
            {
                if (typists[i] != null)
                {
                    double wpm = race.calculateWPM(typists[i]);

                    JLabel label = new JLabel(
                            typists[i].getName() + ": " + String.format("%.1f", wpm) + " WPM"
                    );

                    label.setFont(label.getFont().deriveFont(14f));
                    label.setForeground(typists[i].getColor());
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);

                    listPanel.add(label);
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        return listPanel;
    }

    private JPanel buildButtons()
    {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton backButton = new JButton("Back to Race");
        backButton.addActionListener(e -> parentFrame.showRace());

        buttonPanel.add(backButton);

        return buttonPanel;
    }

    /**
     * Returns the built stats panel.
     *
     * @return the stats panel
     */
    public JPanel getPanel()
    {
        return mainPanel;
    }

    /**
     * Draws a line graph showing WPM over time for each typist.
     */
    private static class WpmLineGraphPanel extends JPanel
    {
        private Game race;

        public WpmLineGraphPanel(Game race)
        {
            this.race = race;
            setBorder(BorderFactory.createTitledBorder("WPM Over Race"));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            Typist[] typists = race.getSeats();
            RaceStats stats = race.getRaceStats();

            if (typists == null || typists.length == 0 || stats == null)
            {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            int leftPadding = 70;
            int bottomPadding = 60;
            int topPadding = 40;
            int rightPadding = 30;

            int graphWidth = width - leftPadding - rightPadding;
            int graphHeight = height - topPadding - bottomPadding;

            int maxTurns = Math.max(1, stats.getLongestHistory());
            double maxWpm = 1;

            for (int i = 0; i < typists.length; i++)
            {
                if (typists[i] != null)
                {
                    ArrayList<Double> history = stats.getWpmHistory(i);

                    for (int j = 0; j < history.size(); j++)
                    {
                        maxWpm = Math.max(maxWpm, history.get(j));
                    }
                }
            }

            // graph axes
            g2.setColor(Color.BLACK);
            g2.drawLine(leftPadding, topPadding, leftPadding, topPadding + graphHeight);
            g2.drawLine(leftPadding, topPadding + graphHeight, leftPadding + graphWidth, topPadding + graphHeight);

            // axis labels
            g2.drawString("WPM", 20, topPadding + 10);
            g2.drawString("Turns", leftPadding + graphWidth - 30, topPadding + graphHeight + 40);

            // y-axis labels
            for (int i = 0; i <= 5; i++)
            {
                int y = topPadding + graphHeight - (i * graphHeight / 5);
                double labelValue = maxWpm * i / 5.0;

                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(leftPadding, y, leftPadding + graphWidth, y);

                g2.setColor(Color.BLACK);
                g2.drawString(String.format("%.0f", labelValue), 25, y + 5);
            }

            // x-axis labels
            for (int i = 0; i <= 5; i++)
            {
                int x = leftPadding + (i * graphWidth / 5);
                int turnLabel = (int) ((double) maxTurns * i / 5.0);

                g2.setColor(Color.BLACK);
                g2.drawString(String.valueOf(turnLabel), x - 5, topPadding + graphHeight + 20);
            }

            // draw each typist line
            for (int i = 0; i < typists.length; i++)
            {
                if (typists[i] != null)
                {
                    ArrayList<Double> history = stats.getWpmHistory(i);

                    if (history.size() < 2)
                    {
                        continue;
                    }

                    g2.setColor(typists[i].getColor());
                    g2.setStroke(new BasicStroke(3));

                    for (int j = 1; j < history.size(); j++)
                    {
                        int x1 = leftPadding + (int) (((double) (j - 1) / (maxTurns - 1)) * graphWidth);
                        int y1 = topPadding + graphHeight - (int) ((history.get(j - 1) / maxWpm) * graphHeight);

                        int x2 = leftPadding + (int) (((double) j / (maxTurns - 1)) * graphWidth);
                        int y2 = topPadding + graphHeight - (int) ((history.get(j) / maxWpm) * graphHeight);

                        g2.drawLine(x1, y1, x2, y2);
                    }

                    // label line at the end
                    double lastWpm = history.get(history.size() - 1);
                    int endX = leftPadding + (int) (((double) (history.size() - 1) / (maxTurns - 1)) * graphWidth);
                    int endY = topPadding + graphHeight - (int) ((lastWpm / maxWpm) * graphHeight);

                    g2.drawString(String.valueOf(typists[i].getSymbol()), endX + 5, endY);
                }
            }
        }
    }
}