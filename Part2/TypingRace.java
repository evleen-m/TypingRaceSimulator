import java.util.Arrays;
import java.lang.Math;
import java.util.Random;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * @author Evleen Manghera
 * @version 1
 */
public class TypingRace {

    private int passageLength;
    private String passageText;
    private Typist[] seats;
    private int numberOfSeats;

    private final double MISTYPE_BASE_CHANCE = 0.3;
    private final int BURNOUT_DURATION = 3;

    private int slideBackAmount = 2;
    private double burnoutRisk = 0.05;

    private int turnCount = 0;
    private int finishPositionCounter = 0;
    private boolean finished = false;
    private Typist winner = null;

    private RaceStats raceStats;

    // difficulty modifiers
    private boolean autocorrectOn = false;
    private boolean nightShiftOn = false;
    private boolean coffeeModeOn = false;

    private final Names[] name = {
            new Names("TURBO-FINGERS"),
            new Names("QWERTY_QUEEN"),
            new Names("HUNT_N_PECK"),
            new Names("KEY-SMASHER"),
            new Names("SPEED_TYPER"),
            new Names("CTRL_MASTER"),
            new Names("SHIFT_WARRIOR"),
            new Names("BYTE_BLAZER"),
            new Names("CODE_TYPHOON"),
            new Names("RAPID_KEYS")
    };

    private boolean raceSavedToHistory = false;

    // empty game object
    public TypingRace() {
    }

    public void StartRace(GameConfig config, TypistConfig[] typistConfigs)
    {
        resetRace(typistConfigs);

        numberOfSeats = config.playerNumber;
        setPassageText(config.passageText);

        seats = new Typist[numberOfSeats];
        Arrays.fill(seats, null);

        if (raceStats == null)
        {
            raceStats = new RaceStats();
        }

        autocorrectOn = config.autocorrectOn;
        coffeeModeOn = config.coffeeModeOn;
        nightShiftOn = config.nightShiftOn;

        if (autocorrectOn)
        {
            slideBackAmount = slideBackAmount / 2;
        }

        createTypists(typistConfigs);
    }

    private String pickTypistName() {
        Random rand = new Random();
        int index = rand.nextInt(name.length);

        while (name[index].isUsed() ){
            index = rand.nextInt(name.length);
        }

        name[index].setUsed(true);
        return name[index].getName();
    }

    private void createTypists(TypistConfig[] typistConfigs)
    {
        for (int i = 0; i < numberOfSeats; i++)
        {
            String chosenSymbol = typistConfigs[i].getSymbol();
            char symbol = chosenSymbol.charAt(0);

            Typist typist = new Typist(pickTypistName(), symbol);

            if (nightShiftOn)
            {
                typist.setAccuracy(typist.getAccuracy() - 0.07);
            }

            typist.setStartingAccuracy(typist.getAccuracy());
            typist.setConfig(typistConfigs[i]);
            typist.setColor(typistConfigs[i].getColour());

            applyRankImpact(typist);
            typist.setStartingAccuracy(typist.getAccuracy());

            addTypist(typist, i);
        }
    }

    /**
     * Seats a typist at the given seat number.
     *
     * @param theTypist  the typist to seat
     * @param seatNumber the seat in which to place them
     */
    public void addTypist(Typist theTypist, int seatNumber) {
        if (seatNumber >= numberOfSeats) {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
            return;
        }

        seats[seatNumber] = theTypist;
    }

    /**
     * Resets the race state so that a new race can begin.
     */
    public void resetRace(TypistConfig[] typistConfigs)
    {
        turnCount = 0;
        finishPositionCounter = 0;
        finished = false;
        winner = null;
        autocorrectOn = false;
        nightShiftOn = false;
        coffeeModeOn = false;
        slideBackAmount = 2;
        raceSavedToHistory = false;

        if (raceStats != null)
        {
            raceStats.reset();
        }

        if (seats != null)
        {
            for (int i = 0; i < numberOfSeats; i++)
            {
                if (seats[i] != null)
                {
                    seats[i].resetToStart();
                }
            }
        }

        resetTypistColors(typistConfigs);

        // reset names
        for (Names names : name)
        {
            names.setUsed(false);
        }
    }

    public void resetTypistColors(TypistConfig[] typistConfigs)
    {
        if (seats != null && typistConfigs != null)
        {
            for (int i = 0; i < numberOfSeats; i++)
            {
                if (seats[i] != null && i < typistConfigs.length)
                {
                    seats[i].setColor(typistConfigs[i].getColour());
                }
            }
        }
    }

    /**
     * Advances the whole race by one turn.
     */
    // advances the race by one turn for all typists
    public void advanceOneTurn() {
        if (finished) {
            return;
        }

        turnCount++;

        // only move typists who have not finished yet
        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null && !seats[i].isFinished()) {
                seats[i].setJustMistyped(false);
                advanceTypist(seats[i]);
            }
        }

        // check which typists finished this turn
        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null && !seats[i].isFinished() && raceFinishedBy(seats[i])) {

                finishPositionCounter++;

                if (winner == null) {
                    winner = seats[i];
                    winner.setAccuracy(winner.getAccuracy() + 0.02);
                }

                seats[i].setFinished(true);
                seats[i].setFinishTurn(turnCount);
                seats[i].setFinishPosition(finishPositionCounter);
            }
        }

        // record WPM history for graphing
        if (raceStats != null)
        {
            for (int i = 0; i < numberOfSeats; i++) {
                if (seats[i] != null) {
                    raceStats.recordWPM(seats[i].getName(), calculateWPM(seats[i]));
                }
            }
        }

        // end the whole race only when everyone is finished
        finished = checkAllFinished();

        if (finished && !raceSavedToHistory && raceStats != null)
        {
            raceStats.saveCompletedRace(seats, this);
            raceSavedToHistory = true;
        }

    }

    private boolean checkAllFinished() {
        for (int i = 0; i < numberOfSeats; i++) {
            if (seats[i] != null && !seats[i].isFinished()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Simulates one turn for a typist.
     *
     * @param theTypist the typist to advance
     */
    private void advanceTypist(Typist theTypist) {
        //recover from burnout
        if (theTypist.isBurntOut()) {
            theTypist.recoverFromBurnout();
            return;
        }

        TypistConfig config = theTypist.getConfig();

        double accuracy = theTypist.getAccuracy();

        // only use preset accuracies if config it not null
        if (config != null) {
            accuracy = accuracy + config.getAccuracyBonus(passageLength, theTypist.getProgress());
        }
        //clamp accuracy
        accuracy = Math.max(0.0, Math.min(1.0, accuracy));

        double mistypeChance = (1 - accuracy) * MISTYPE_BASE_CHANCE;

        if (config != null)
        {
            mistypeChance = mistypeChance * config.getMistypeMultiplier();
        }

        if (Math.random() < mistypeChance) {
            theTypist.slideBack(slideBackAmount);
            theTypist.setJustMistyped(true);
            theTypist.recordMistype();
        }

        else if (Math.random() < accuracy)
        {
            // set up speed for use in typing
            double speedMultiplier = 1.0;
            if (config != null)
            {
                speedMultiplier = config.getSpeedMultiplier();
            }

            int typed = typeWithSpeed(theTypist, speedMultiplier);
            for (int i = 0; i < typed; i++)
            {
                theTypist.recordCorrectKeystroke();
            }

            // Caffeine Mode: speed boost for the first 10 turns
            if (coffeeModeOn && turnCount <= 10) {
                if (Math.random() < 0.7) {
                    int typed2 = typeWithSpeed(theTypist, speedMultiplier);

                    for (int i = 0; i < typed2; i++)
                    {
                        theTypist.recordCorrectKeystroke();
                    }
                }
            }

        }

        double currentBurnoutRisk = burnoutRisk * accuracy * accuracy;

        if (config != null) {
            currentBurnoutRisk = currentBurnoutRisk * config.getBurnoutMultiplier();
        }

        // Caffeine Mode: after 10 turns, burnout risk increases exponentially
        if (coffeeModeOn && turnCount > 10) {
            currentBurnoutRisk = currentBurnoutRisk * 3;
        }

        if (Math.random() < currentBurnoutRisk) {
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.04);

            // check if writst support -> decrease burnout turns by one if there is
            int duration = BURNOUT_DURATION;
            if (config != null)
            {
                duration += config.getBurnoutDurationModifier();
            }
            duration = Math.max(1, duration);
            theTypist.burnOut(duration);
            theTypist.recordBurnout(turnCount);
        }
    }

    private int typeWithSpeed(Typist theTypist, double speedMultiplier)
    {
        int charactersTyped = 0;

        int guaranteedCharacters = (int) speedMultiplier;
        double extraCharacterChance = speedMultiplier - guaranteedCharacters;

        for (int i = 0; i < guaranteedCharacters; i++)
        {
            theTypist.typeCharacter();
            charactersTyped++;
        }

        if (Math.random() < extraCharacterChance)
        {
            theTypist.typeCharacter();
            charactersTyped++;
        }

        return charactersTyped;
    }

    /**
     * Returns true if the given typist has completed the full passage.
     *
     * @param theTypist the typist to check
     * @return true if their progress has reached or passed the passage length
     */
    private boolean raceFinishedBy(Typist theTypist) {
        return (theTypist.getProgress() >= passageLength);
    }

    /**
     * Calculates the current WPM for a typist.
     *
     * @param t the typist
     * @return the WPM estimate
     */
    public double calculateWPM(Typist t) {
        int typistTurnCount = turnCount;

        if (t.isFinished()) {
            typistTurnCount = t.getFinishTurn();
        }

        if (typistTurnCount == 0) {
            return 0;
        }

        double minutes = (typistTurnCount * 0.2) / 60.0;
        double words = t.getProgress() / 5.0;

        return words / minutes;
    }

    /**
     * Sets the passage text used in the race.
     *
     * @param text the new passage text
     */
    public void setPassageText(String text) {
        if (text == null || text.trim().isEmpty()) {
            passageText = "The quick brown fox jumps over the lazy dog.";
        } else {
            passageText = text.trim();
        }

        passageLength = passageText.length();
    }

    public int getPassageLength() {
        return passageLength;
    }

    public String getPassageText() {
        return passageText;
    }

    public Typist[] getSeats() {
        return seats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public boolean isFinished() {
        return finished;
    }

    public Typist getWinner() {
        return winner;
    }

    public RaceStats getRaceStats()
    {
        return raceStats;
    }

    public String getModifiers()
    {
        String msg = "";
        if (autocorrectOn){
            msg = msg + "\n Autocorrect";
        }
        if (coffeeModeOn){
            msg = msg + "\n Coffee Mode";
        }
        if (nightShiftOn){
            msg = msg + "\n Night Shift";
        }

        if (msg.isEmpty()) {
            return " N/A";
        }
        return msg;
    }




    /**
     * changes starting accuracy based on previous race performance
     * higher ranked typists get a small accuracy penalty
     *
     * @param typist the typist being adjusted
     */
    private void applyRankImpact(Typist typist)
    {
        if (raceStats == null)
        {
            return;
        }

        int points = raceStats.getCumulativePoints(typist.getName());

        // champions face higher expectations
        if (points >= 25)
        {
            typist.setAccuracy(typist.getAccuracy() - 0.04);
        }
        else if (points >= 15)
        {
            typist.setAccuracy(typist.getAccuracy() - 0.02);
        }
        else if (points <= 5 && points > 0)
        {
            typist.setAccuracy(typist.getAccuracy() + 0.02);
        }
    }
}