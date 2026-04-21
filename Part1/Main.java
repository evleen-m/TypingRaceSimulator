public class Main
{
    public static void main(String[] args)
    {
        // Create a typist
        Typist t = new Typist('①', "TURBOFINGERS", 0.8);

        System.out.println("test 1: Normal forward movement");
        t.typeCharacter();
        t.typeCharacter();
        t.typeCharacter();
        System.out.println("Progress: " + t.getProgress());


        System.out.println("\n test 2: progress does not go below 0 after calling slideBack() ");
        // progress set to three from test 1
        t.slideBack(2);
        System.out.println("Progress: " + t.getProgress());
        t.slideBack(5); // more than current progress (progress = 1)
        System.out.println("Progress: " + t.getProgress());


        System.out.println("\n test 3: burnout countdown");
        t.burnOut(3);
        System.out.println("Burnout turns remaining: " + t.getBurnoutTurnsRemaining());
        for (int i = 1; i <= 4; i++)
        {
            t.recoverFromBurnout();
            System.out.println("After turn " + i +
                    ", remaining: " + t.getBurnoutTurnsRemaining() +
                    ", isBurntOut: " + t.isBurntOut());
        }

        // test 4
        System.out.println("\n test 4: resetToStart()");
        t.typeCharacter();
        t.typeCharacter();
        t.burnOut(2);
        System.out.println("progress: " + t.getProgress());
        System.out.println("burntOut:" + t.isBurntOut());

        t.resetToStart();
        System.out.println("Progress should be 0: " + t.getProgress());
        System.out.println("Burnout should be false: " + t.isBurntOut());
        System.out.println("Burnout turns should be 0: " + t.getBurnoutTurnsRemaining());

        // -------------------------------
        // Test 5: Accuracy bounds
        // -------------------------------
        System.out.println("\n test 5: setAccuracy()");
        t.setAccuracy(1.5);
        System.out.println("Accuracy should be capped at 1.0: " + t.getAccuracy());
        t.setAccuracy(-0.5);
        System.out.println("Accuracy should be floored at 0.0: " + t.getAccuracy());
        t.setAccuracy(0.75);
        System.out.println("Accuracy should be 0.75: " + t.getAccuracy());
    }
}