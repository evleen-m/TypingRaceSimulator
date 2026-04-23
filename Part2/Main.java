import javax.swing.SwingUtilities;
/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * @author Evleen Manghera
 * @version 1
 */
public class Main
{
    public static void main(String[] args)
    {
        Game race = new Game(4, "HI");
        startRaceGUI(race);
    }

    /**
     * Starts the GUI version of the race.
     */
    public static void startRaceGUI(Game race)
    {
        SwingUtilities.invokeLater(() -> {
            FrameManager frame = new FrameManager(race);
            frame.show();
        });
    }
}