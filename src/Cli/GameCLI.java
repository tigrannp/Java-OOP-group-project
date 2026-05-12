package Cli;

import Core.GameEngine;
import Core.Team;
import Core.Unit;
import Core.SupportUnit;

import java.util.ArrayList;
import java.util.Scanner;

public class GameCLI {

    private GameEngine engine;

    public GameCLI() {
        this.engine = new GameEngine();
    }

    public void run() {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Grid Strategy - Defenders (CLI Mode) ===");

        while (engine.getCurrentTurn() != null) {
            printBoard(null);
            printInstructions();

            System.out.print("Select a unit (row col): ");
            String selectLine = sc.nextLine().trim();

            if (selectLine.equals("end")) {
                System.out.println("Ending player turn...");
                engine.passTurn();
                printEnemyLog();
                continue;
            }

            String[] selectParts = selectLine.split(" ");
            if (selectParts.length != 2) {
                System.out.println("Invalid input. Enter row and col (e.g. '2 1') or 'end'.");
                continue;
            }

            int selRow, selCol;
            try {
                selRow = Integer.parseInt(selectParts[0]);
                selCol = Integer.parseInt(selectParts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Use numbers for row and col.");
                continue;
            }

            Unit selected = engine.getUnitAt(selRow, selCol);
            if (selected == null || selected.getTeam() != Team.PLAYER) {
                System.out.println("No friendly unit at that position.");
                continue;
            }

            printBoard(selected);
            System.out.println("Selected: " + selected.getName() +
                    " | HP: " + selected.getHp() + "/" + selected.getMaxHp() +
                    " | Power: " + selected.getPower() +
                    " | Move: " + selected.getMoveRange() +
                    " | Attack: " + selected.getAttackRange() +
                    " | Role: " + (selected instanceof SupportUnit ? "Support" : "Combat"));
            printActionInstructions(selected);

            System.out.print("Action: ");
            String actionLine = sc.nextLine().trim();
            String[] actionParts = actionLine.split(" ");

            if (actionParts.length == 0 || actionLine.isEmpty()) {
                System.out.println("No action entered.");
                continue;
            }

            String action = actionParts[0].toLowerCase();

            if (action.equals("end")) {
                System.out.println("Ending player turn...");
                engine.passTurn();
                printEnemyLog();
                continue;
            }

            if (action.equals("back")) {
                continue;
            }

            if (!action.equals("move") && !action.equals("attack") && !action.equals("heal")) {
                System.out.println("Unknown action. Use move, attack, heal, back, or end.");
                continue;
            }

            if (actionParts.length != 3) {
                System.out.println("Usage: " + action + " <row> <col>");
                continue;
            }

            int targetRow, targetCol;
            try {
                targetRow = Integer.parseInt(actionParts[1]);
                targetCol = Integer.parseInt(actionParts[2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid row/col. Use numbers.");
                continue;
            }

            engine.handleClick(selRow, selCol);
            engine.handleClick(targetRow, targetCol);

            System.out.println(">> " + engine.getStatusMessage());

            if (engine.getCurrentTurn() == null) {
                printBoard(null);
                System.out.println("\n" + engine.getStatusMessage());
                break;
            }
        }

        System.out.println("Thanks for playing!");
    }

    private void printBoard(Unit highlighted) {
        System.out.println();
        ArrayList<Unit> units = engine.getUnits();

        System.out.print("    ");
        for (int c = 0; c < 12; c++) {
            System.out.printf("%2d ", c);
        }
        System.out.println();

        System.out.print("   ");
        for (int c = 0; c < 12; c++) {
            System.out.print("---");
        }
        System.out.println("-");

        for (int r = 0; r < 7; r++) {
            System.out.printf("%2d |", r);
            for (int c = 0; c < 12; c++) {
                Unit u = getUnitAt(units, r, c);

                if (highlighted != null && u != null
                        && highlighted.getRow() == r && highlighted.getCol() == c) {
                    System.out.print("\u001b[33m[" + u.getSymbol() + "]\u001b[0m");
                } else if (u != null && u.getTeam() == Team.PLAYER) {
                    System.out.print("\u001b[34m[" + u.getSymbol() + "]\u001b[0m");
                } else if (u != null && u.getTeam() == Team.ENEMY) {
                    System.out.print("\u001b[31m[" + u.getSymbol() + "]\u001b[0m");
                } else {
                    System.out.print(" . ");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Status: " + engine.getStatusMessage());
        System.out.println();
    }

    private void printEnemyLog() {
        System.out.println("--- Enemy Turn Complete ---");
        System.out.println(">> " + engine.getStatusMessage());

        if (engine.getCurrentTurn() == null) {
            printBoard(null);
            System.out.println("\n" + engine.getStatusMessage());
        }
    }

    private void printInstructions() {
        System.out.println("-------------------------------------------------------");
        System.out.println("  UNITS:  [K] Knight  [A] Archer  [C] Cleric  (blue = yours)");
        System.out.println("          [O] Orc     [G] Goblin               (red = enemy)");
        System.out.println("  HOW TO PLAY:");
        System.out.println("    1. Type the row and col of your unit to select it  (e.g. '2 1')");
        System.out.println("    2. Then type an action:  move <row> <col>          (e.g. 'move 2 3')");
        System.out.println("                             attack <row> <col>        (e.g. 'attack 2 10')");
        System.out.println("                             heal <row> <col>          (e.g. 'heal 3 1')");
        System.out.println("                             back   (reselect a unit)");
        System.out.println("                             end    (end your turn)");
        System.out.println("-------------------------------------------------------");
    }

    private void printActionInstructions(Unit unit) {
        if (unit instanceof SupportUnit) {
            System.out.println("  Actions: move <row> <col> | attack <row> <col> | heal <row> <col> | back | end");
        } else {
            System.out.println("  Actions: move <row> <col> | attack <row> <col> | back | end");
        }
    }

    private Unit getUnitAt(ArrayList<Unit> units, int r, int c) {
        for (int i = 0; i < units.size(); i++) {
            Unit u = units.get(i);
            if (u.getRow() == r && u.getCol() == c) return u;
        }
        return null;
    }
}