import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * @author Evleen Manghera
 * @version 1
 */
public class TypingRace
{
    private int passageLength;   // Total characters in the passage to type
    private Typist[] seats;
    private int numberOfSeats;

    // Accuracy thresholds for mistype and burnout events
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 3;
    private static final int    BURNOUT_DURATION     = 3;

    private int turnCount = 0;

    /**
     * Constructor for objects of class TypingRace.
     * Sets up the race with a passage of the given length.
     * Initially, there are no typists seated.
     *
     * @param passageLength the number of characters in the passage to type
     */
    public TypingRace(int passageLength,int seatsLength)
    {
        if (passageLength > 0)
        {
            this.passageLength = passageLength;
        }
        else
        {
            this.passageLength = 40;
        }
        // index 0 is not used
        seats = new Typist[seatsLength+1];
        Arrays.fill(seats, null);
        this.numberOfSeats = seatsLength+1;
    }

    /**
     * Seats a typist at the given seat number.
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat in which to place them
     */
    public void addTypist(Typist theTypist, int seatNumber)
    {
        //index 0 not used
        if (seatNumber == 0)
        {
            System.out.println("cannot seat typist at seat 0");
        }

        //add typist to seat
        seats[seatNumber] = theTypist;

        if (seatNumber > numberOfSeats)
        {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }

    /**
     * Starts the typing race.
     * All typists are reset to the beginning, then the simulation runs
     * turn by turn until one typist completes the full passage.
     */
    public void startRace()
    {
        boolean finished = false;
        Typist winner = null;

        // Reset all typists to the start of the passage
        for  (int i = 1; i < numberOfSeats; i++){
            if (seats[i] != null){
            seats[i].resetToStart();
            }
        }

        //game loop
        while (!finished)
        {
            turnCount++;
            // Advance each typist by one turn
            for  (int i = 1; i < numberOfSeats; i++){
                // remove mistyped marker
                if (seats[i] != null){
                    seats[i].setJustMistyped(false);
                    advanceTypist(seats[i]);
                }
            }

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage
            // Check for winner
            for  (int i = 1; i < numberOfSeats; i++){
                if (seats[i] != null){
                    if(raceFinishedBy(seats[i])){
                        winner = seats[i];
                        finished = true;
                    }
                }
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        // Print the winner's name + new accuracy
        if (winner != null) {
            System.out.println();
            System.out.println("And the winner is... " + winner.getName() + "!");

            double oldAccuracy = winner.getAccuracy();
            winner.setAccuracy(winner.getAccuracy() + 0.02);
            double newAccuracy = winner.getAccuracy();
            System.out.println("Final accuracy: " + newAccuracy +" (improved from "  + oldAccuracy +")");
        }
    }

    /**
     * Simulates one turn for a typist.
     *
     * If the typist is burnt out, they recover one turn's worth and skip typing.
     * Otherwise:
     *   - They may type a character (advancing progress) based on their accuracy.
     *   - They may mistype (sliding back) — the chance of mistype should decrease
     *     for more accurate typists.
     *   - They may burn out — more likely for very high-accuracy typists
     *     who are pushing themselves too hard.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(Typist theTypist)
    {
        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Mistype check — the probability should reflect the typist's accuracy
        if (Math.random() < (1 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
            theTypist.setJustMistyped(true);
        }
        else if (Math.random() < theTypist.getAccuracy())
        {
            //type character
            theTypist.typeCharacter();
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.02);
            theTypist.burnOut(BURNOUT_DURATION);
        }
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist)
    {
        return (theTypist.getProgress() >= passageLength);
    }

    /**
     * Prints the current state of the race to the terminal.
     * Shows each typist's position along the passage, burnout state,
     * and a WPM estimate based on current progress.
     */
    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE — passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        for  (int i = 1; i < numberOfSeats; i++){
            if (seats[i] != null){
                printSeat(seats[i]);
                System.out.println();
            }
        }

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     * @param theTypist the typist whose lane to print
     */
    private void printSeat(Typist theTypist)
    {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter  = passageLength - theTypist.getProgress();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen
        System.out.print(theTypist.getSymbol());

        // print player mistyped
        if (theTypist.hasJustMistyped()){
            System.out.print('<');
            spacesAfter--;
        }

        //print player burnt out
        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--; // symbol + ~ together take two characters
        }

        //print empty spaces
        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else
        {
            System.out.print(theTypist.getName() + " (Accuracy: "
                    + String.format("%.2f",theTypist.getAccuracy())
                    + ", WPM: " + String.format("%.2f", calculateWPM(theTypist)) + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     *
     * @param aChar the character to print
     * @param times how many times to print it
     */
    private void multiplePrint(char aChar, int times)
    {
        for (int i = 0; i < times; i++)
        {
            System.out.print(aChar);
        }
    }

    /**
     * Calculates the current wpm for a typist and returns this
     * @param t the typist on which wpm is calculated for
     */
    private double calculateWPM(Typist t)
    {
        if (turnCount == 0) return 0;

        double minutes = (turnCount * 0.2) / 60.0; // each turn = 200ms
        double words = t.getProgress() / 5.0; // standard: 5 chars = 1 word

        return words / minutes;
    }



    public static void main(String[] args) {
        TypingRace race = new TypingRace(40,3);
        race.addTypist(new Typist('①', "TURBOFINGERS", 0.85), 1);
        race.addTypist(new Typist('②', "QWERTY_QUEEN",  0.60), 2);
        race.addTypist(new Typist('③', "HUNT_N_PECK",   0.30), 3);
        race.startRace();
    }
}
