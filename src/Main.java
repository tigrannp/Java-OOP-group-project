import Cli.GameConsole;

/**
 * Entry point for the Grid Strategy - Defenders application.
 */
public class Main {

    /**
     * Launches the game. Starts in GUI mode by default,
     * or CLI mode if the {@code -console} argument is passed.
     *
     * @param args pass {@code -console} to launch in CLI mode
     */
    public static void main(String[] args) {
        // args = new String[]{"-console"};
        if (args.length == 0) {
            GameConsole.startGUI();
        } else if (args[0].equals("-console")) {
            GameConsole.startCLI();
        }
    }
}
