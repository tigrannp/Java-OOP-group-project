import Cli.GameConsole;

/**
 * Entry point for the Grid Strategy - Defenders application.
 */
public class Main {
    /**
     * Launches the game in CLI mode.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        if(args.length == 0){
            GameConsole.startGUI();
        }else if(args[0].equals("-console")){
            GameConsole.startCLI();
        }
    }
}
