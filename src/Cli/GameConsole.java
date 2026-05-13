package Cli;

import UI.GameWindow;

/**
 * Entry point for selecting and launching a game mode (GUI or CLI).
 */
public class GameConsole {

    /**
     * Launches the game in graphical (GUI) mode.
     */
    public static void startGUI() {
        System.out.println("Starting GUI Game Mode...");
        GameWindow game = new GameWindow();
        game.setVisible(true);
    }

    /**
     * Launches the game in command-line (CLI) mode.
     */
    public static void startCLI() {
        System.out.println("Starting CLI Game Mode...");
        GameCLI cli = new GameCLI();
        cli.run();
    }
}
