package Cli;

import Core.*;

import java.util.ArrayList;
import java.util.Scanner;

public class GameCLI {

    private GameEngine engine;

    public GameCLI() {
        run();
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Grid Strategy - Defenders ===");

        ArrayList<Unit> templates = UnitDatabase.loadUnits(GameEngine.databasePath);
        UnitPlacementSession session = new UnitPlacementSession(templates);

        runPlacementPhase(sc, session);

        this.engine = new GameEngine(session.getPlacedUnits());

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

    private void runPlacementPhase(Scanner sc, UnitPlacementSession session) {
        System.out.println("\n=== Unit Placement Phase ===");
        System.out.println("Place your units on the left half of the board (cols 0-5).");
        System.out.println("Commands:");
        System.out.println("  place <Knight|Archer|Cleric> <row> <col>");
        System.out.println("  remove <row> <col>");
        System.out.println("  status");
        System.out.println("  done   (finalize, minimum 1 unit required)");
        System.out.println("  end");
        System.out.println();

        while (true) {
            System.out.print("Placement> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(" ");
            String cmd = parts[0].toLowerCase();

            if (cmd.equals("done")) {
                if (!session.isReady()) {
                    System.out.println("You need at least 1 unit before starting.");
                    continue;
                }
                System.out.println("Placement finalized. Starting game!\n");
                break;

            } else if (cmd.equals("status")) {
                System.out.println(session.getSummary());

            }else if (cmd.equals("end")) {
                System.exit(0);

            } else if (cmd.equals("remove")) {
                if (parts.length != 3) {
                    System.out.println("Usage: remove <row> <col>");
                    continue;
                }
                try {
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    boolean removed = session.removeUnit(row, col);
                    System.out.println(removed ? "Unit removed." : "No unit at that position.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid row/col.");
                }

            } else if (cmd.equals("place")) {
                if (parts.length != 4) {
                    System.out.println("Usage: place <Knight|Archer|Cleric> <row> <col>");
                    continue;
                }
                String name = parts[1];
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                try {
                    int row = Integer.parseInt(parts[2]);
                    int col = Integer.parseInt(parts[3]);
                    PlacementResult result = session.placeUnit(name, row, col);
                    switch (result) {
                        case SUCCESS:
                            System.out.println(name + " placed at (" + row + ", " + col + ").");
                            System.out.println(session.getSummary());
                            break;
                        case EXCEED_TOTAL:
                            System.out.println("Maximum of 5 units reached.");
                            break;
                        case EXCEED_TYPE:
                            System.out.println("Maximum of that unit type reached.");
                            break;
                        case OUT_OF_BOUNDS:
                            System.out.println("Position out of bounds. Rows: 0-6, Cols: 0-5.");
                            break;
                        case TILE_TAKEN:
                            System.out.println("That tile is already occupied.");
                            break;
                        case INVALID_TYPE:
                            System.out.println("Unknown unit type. Choose Knight, Archer, or Cleric.");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid row/col.");
                }

            } else {
                System.out.println("Unknown command. Use place, remove, status, or done.");
            }
        }
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