import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Draws a comparison graph for historical typist data.
 */
public class ComparisonGraphPanel extends JPanel
{
    //
    private Game race;
    private String metric;

    public ComparisonGraphPanel(Game race, String metric)
    {
        this.race = race;
        this.metric = metric;
        setBorder(BorderFactory.createTitledBorder("Comparison View"));
        setBackground(Color.WHITE);
    }

    public void setMetric(String metric)
    {
        this.metric = metric;
        repaint();
    }

    /**
     * Paints the comparison graph.
     *
     * @param g the graphics object
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Typist[] typists = race.getSeats();
        RaceStats stats = race.getRaceStats();

        if (typists == null || stats == null)
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
        int bottomPadding = 70;
        int topPadding = 40;
        int rightPadding = 30;

        int graphWidth = width - leftPadding - rightPadding;
        int graphHeight = height - topPadding - bottomPadding;

        double maxValue = 1;
        int maxRaces = 1;

        // find the largest value so the graph can scale correctly
        for (int i = 0; i < typists.length; i++)
        {
            if (typists[i] != null)
            {
                ArrayList<RaceRecord> history = stats.getRaceHistory(typists[i].getName());
                maxRaces = Math.max(maxRaces, history.size());

                for (int j = 0; j < history.size(); j++)
                {
                    maxValue = Math.max(maxValue, getMetricValue(history.get(j), stats));
                }
            }
        }

        // graph axes
        g2.setColor(Color.BLACK);
        g2.drawLine(leftPadding, topPadding, leftPadding, topPadding + graphHeight);
        g2.drawLine(leftPadding, topPadding + graphHeight, leftPadding + graphWidth, topPadding + graphHeight);

        // axis labels
        g2.drawString(metric, 20, topPadding + 10);
        g2.drawString("Race Number", leftPadding + graphWidth - 80, topPadding + graphHeight + 45);

        // y-axis guide lines
        for (int i = 0; i <= 5; i++)
        {
            int y = topPadding + graphHeight - (i * graphHeight / 5);
            double labelValue = maxValue * i / 5.0;

            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(leftPadding, y, leftPadding + graphWidth, y);

            g2.setColor(Color.BLACK);
            g2.drawString(String.format("%.0f", labelValue), 25, y + 5);
        }

        // draw each typist line
        for (int i = 0; i < typists.length; i++)
        {
            if (typists[i] != null)
            {
                ArrayList<RaceRecord> history = stats.getRaceHistory(typists[i].getName());

                if (history.size() < 1)
                {
                    continue;
                }

                Color typistColor = typists[i].getColor();

                // fallback in case the typist colour is null
                if (typistColor == null)
                {
                    typistColor = new Color(60, 140, 255);
                }

                g2.setColor(typistColor);
                g2.setStroke(new BasicStroke(3));

                for (int j = 1; j < history.size(); j++)
                {
                    double value1 = getMetricValue(history.get(j - 1), stats);
                    double value2 = getMetricValue(history.get(j), stats);

                    int x1 = leftPadding + (int) (((double) (j - 1) / Math.max(1, maxRaces - 1)) * graphWidth);
                    int y1 = topPadding + graphHeight - (int) ((value1 / maxValue) * graphHeight);

                    int x2 = leftPadding + (int) (((double) j / Math.max(1, maxRaces - 1)) * graphWidth);
                    int y2 = topPadding + graphHeight - (int) ((value2 / maxValue) * graphHeight);

                    g2.drawLine(x1, y1, x2, y2);
                }

                // draw points for each completed race
                for (int j = 0; j < history.size(); j++)
                {
                    double value = getMetricValue(history.get(j), stats);

                    int x = leftPadding + (int) (((double) j / Math.max(1, maxRaces - 1)) * graphWidth);
                    int y = topPadding + graphHeight - (int) ((value / maxValue) * graphHeight);

                    g2.fillOval(x - 4, y - 4, 8, 8);
                }

                // label the final point with the typist symbol
                RaceRecord lastRecord = history.get(history.size() - 1);
                double lastValue = getMetricValue(lastRecord, stats);

                int endX = leftPadding + (int) (((double) (history.size() - 1) / Math.max(1, maxRaces - 1)) * graphWidth);
                int endY = topPadding + graphHeight - (int) ((lastValue / maxValue) * graphHeight);

                g2.drawString(String.valueOf(typists[i].getSymbol()), endX + 6, endY);
            }
        }
    }

    /**
     * Gets the selected metric value from a race record.
     *
     * @param record the race record
     * @return the chosen metric value
     */
    private double getMetricValue(RaceRecord record, RaceStats stats)
    {
        if (metric.equals("Accuracy"))
        {
            return record.getAccuracyPercentage();
        }
        else if (metric.equals("Burnouts"))
        {
            return record.getBurnoutCount();
        }
        else if (metric.equals("Finish Position"))
        {
            return record.getFinishPosition();
        }
//        else if (metric.equals("Accuracy Change"))
//        {
//            return record.getAccuracyChange();
//        }
//        else if (metric.equals("Personal Best WPM"))
//        {
//            return stats.getPersonalBestWpm(record.getTypistName());
//        }

        return record.getWpm();
    }
}