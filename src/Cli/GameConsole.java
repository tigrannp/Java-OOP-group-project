package Cli;

import UI.GameWindow;

public class GameConsole {

    public static void startGUI() {
        System.out.println("Starting GUI Game Mode...");
        GameWindow game = new GameWindow();
        game.setVisible(true);
    }

    public static void startCLI() {
        System.out.println("Starting CLI Game Mode...");
        GameCLI cli = new GameCLI();
        cli.run();
    }
}