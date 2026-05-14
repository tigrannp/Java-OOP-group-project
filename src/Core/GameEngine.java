package Core;

import java.util.ArrayList;

/**
 * Core game logic controller.
 * Manages units, turn order, player input handling, enemy AI, and win conditions.
 */
public class GameEngine {

    /** Path to the unit database CSV file. */
    public static final String databasePath = "units.txt";

    private ArrayList<Unit> units;
    private Unit selectedUnit;
    private Team currentTurn;
    private String statusMessage;

    /**
     * Creates a new game engine and sets up the board with the player's units and enemy units.
     *
     * @param playerUnits the units placed by the player during the placement phase
     */
    public GameEngine(ArrayList<Unit> playerUnits) {
        this.units = new ArrayList<Unit>();
        this.currentTurn = Team.PLAYER;
        this.statusMessage = "Welcome! Player's turn. Your units are blue.";
        setupGame(playerUnits);
    }

    /**
     * Returns all units currently on the board.
     *
     * @return list of all active units
     */
    public ArrayList<Unit> getUnits() { return this.units; }

    /**
     * Returns the currently selected unit, or null if none is selected.
     *
     * @return the selected unit
     */
    public Unit getSelectedUnit() { return this.selectedUnit; }

    /**
     * Returns whose turn it currently is, or null if the game has ended.
     *
     * @return current team's turn, or null if game over
     */
    public Team getCurrentTurn() { return this.currentTurn; }

    /**
     * Returns the latest status message describing the last action or game state.
     *
     * @return status message string
     */
    public String getStatusMessage() { return this.statusMessage; }

    /**
     * Returns the unit at the given board position, or null if the tile is empty.
     *
     * @param r the row to check
     * @param c the column to check
     * @return the unit at (r, c), or null if none
     */
    public Unit getUnitAt(int r, int c) {
        for (int i = 0; i < units.size(); i++) {
            Unit u = units.get(i);
            if (u.getRow() == r && u.getCol() == c) return u;
        }
        return null;
    }

    /**
     * Handles a click at the given board position.
     * The first click selects a friendly unit; the second click performs a move, attack, or heal
     * depending on the target tile.
     *
     * @param r the clicked row
     * @param c the clicked column
     */
    public void handleClick(int r, int c) {
        Unit clickedUnit = getUnitAt(r, c);

        int dist = (selectedUnit != null) ? getDistance(selectedUnit.getRow(), selectedUnit.getCol(), r, c) : -1;

        if (clickedUnit != null && clickedUnit.getTeam() == Team.PLAYER) {
            if (selectedUnit != null) {
                performHeal(clickedUnit, dist);
            } else {
                selectedUnit = clickedUnit;
                statusMessage = "Selected " + selectedUnit.getName();
            }
        }

        else if (selectedUnit != null && selectedUnit.getTeam() == Team.PLAYER) {
            if (clickedUnit == null) {
                performMove(r, c, dist);
            } else if (clickedUnit.getTeam() == Team.ENEMY) {
                performAttack(clickedUnit, dist);
            }
        }

        checkWinCondition();
    }

    /**
     * Ends the player's turn, runs the enemy AI, removes dead units, and starts the next player turn.
     * Also checks win conditions after the enemy acts.
     */
    public void passTurn() {
        currentTurn = Team.ENEMY;
        selectedUnit = null;

        for (int i = 0; i < units.size(); i++) {
            Unit enemy = units.get(i);
            if (enemy.getTeam() == Team.ENEMY && enemy.getHp() > 0) {
                executeAI(enemy);
            }
        }

        for (int i = units.size() - 1; i >= 0; i--) {
            if (units.get(i).getHp() <= 0) units.remove(i);
        }

        checkWinCondition();

        for (int i = 0; i < units.size(); i++) {
            Unit u = units.get(i);
            if (u.getTeam() == Team.PLAYER) {
                u.setHasMoved(false);
                u.setHasActed(false);
            }
        }

        if (currentTurn != null) {
            currentTurn = Team.PLAYER;
            statusMessage = "Player Turn! Select a unit.";
        }
    }

    private void setupGame(ArrayList<Unit> playerUnits) {
        for (Unit u : playerUnits) {
            units.add(u);
        }

        addUnit(new Unit("Orc", "O", 25, 8, 2, 1, Team.ENEMY), 2, 10);
        addUnit(new Unit("Orc", "O", 25, 8, 2, 1, Team.ENEMY), 3, 9);
        addUnit(new Unit("Orc", "O", 25, 8, 2, 1, Team.ENEMY), 4, 10);
        addUnit(new Unit("Goblin", "G", 15, 5, 3, 3, Team.ENEMY), 1, 11);
        addUnit(new Unit("Goblin", "G", 15, 5, 3, 3, Team.ENEMY), 5, 11);
    }

    private void addUnit(Unit u, int r, int c) {
        u.setRow(r);
        u.setCol(c);
        units.add(u);
    }

    private int getDistance(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    private void performMove(int r, int c, int dist) {
        if (dist <= selectedUnit.getMoveRange() && !selectedUnit.getHasMoved()) {
            selectedUnit.move(r, c);
            statusMessage = selectedUnit.getName() + " moved.";
        } else {
            selectedUnit = null;
        }
    }

    private void performAttack(Unit target, int dist) {
        if (dist <= selectedUnit.getAttackRange() && !selectedUnit.getHasActed()) {
            selectedUnit.attack(target);
            statusMessage = "Attacked " + target.getName() + "!";

            if (target.getHp() <= 0) {
                units.remove(target);
                statusMessage = target.getName() + " destroyed!";
            }
            selectedUnit = null;
        } else {
            selectedUnit = null;
        }
    }

    private void performHeal(Unit target, int dist) {
        if (selectedUnit instanceof SupportUnit && target != selectedUnit &&
                dist <= selectedUnit.getAttackRange() && !selectedUnit.getHasActed()) {

            ((SupportUnit) selectedUnit).heal(target);
            selectedUnit.setHasActed(true);
            statusMessage = "Healed " + target.getName() + "!";
            selectedUnit = null;
        } else {
            selectedUnit = target;
            statusMessage = "Selected " + selectedUnit.getName();
        }
    }

    private void executeAI(Unit enemy) {
        Unit target = null;
        int minDistance = 9999;

        for (int i = 0; i < units.size(); i++) {
            Unit p = units.get(i);
            if (p.getTeam() == Team.PLAYER && p.getHp() > 0) {
                int d = getDistance(enemy.getRow(), enemy.getCol(), p.getRow(), p.getCol());
                if (d < minDistance) {
                    minDistance = d;
                    target = p;
                }
            }
        }

        if (target == null) return;

        if (minDistance > enemy.getAttackRange()) {
            int bestRow = enemy.getRow();
            int bestCol = enemy.getCol();
            int bestDistToTarget = minDistance;

            int minRowBound = enemy.getRow() - enemy.getMoveRange();
            int maxRowBound = enemy.getRow() + enemy.getMoveRange();
            int minColBound = enemy.getCol() - enemy.getMoveRange();
            int maxColBound = enemy.getCol() + enemy.getMoveRange();

            for (int r = minRowBound; r <= maxRowBound; r++) {
                for (int c = minColBound; c <= maxColBound; c++) {
                    if (r >= 0 && r < 7 && c >= 0 && c < 12) {
                        int moveDist = getDistance(enemy.getRow(), enemy.getCol(), r, c);
                        if (moveDist <= enemy.getMoveRange() && getUnitAt(r, c) == null) {
                            int h = getDistance(r, c, target.getRow(), target.getCol());
                            if (h < bestDistToTarget) {
                                bestDistToTarget = h;
                                bestRow = r;
                                bestCol = c;
                            }
                        }
                    }
                }
            }

            enemy.setRow(bestRow);
            enemy.setCol(bestCol);
            minDistance = bestDistToTarget;
        }

        if (minDistance <= enemy.getAttackRange()) {
            target.setHp(target.getHp() - enemy.getPower());
        }
    }

    public ArrayList<int[]> getReachableTiles(Unit unit) {
        ArrayList<int[]> tiles = new ArrayList<>();
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 12; c++) {
                int dist = getDistance(unit.getRow(), unit.getCol(), r, c);
                if (!unit.getHasMoved() && dist <= unit.getMoveRange() && getUnitAt(r, c) == null) {
                    tiles.add(new int[]{r, c});
                } else if (!unit.getHasActed() && dist <= unit.getAttackRange() && getUnitAt(r, c) != null && getUnitAt(r, c).getTeam() != unit.getTeam()) {
                    tiles.add(new int[]{r, c});
                }
            }
        }
        return tiles;
    }

    private void checkWinCondition() {
        boolean hasPlayer = false;
        boolean hasEnemy = false;

        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getTeam() == Team.PLAYER) hasPlayer = true;
            if (units.get(i).getTeam() == Team.ENEMY) hasEnemy = true;
        }

        if (!hasPlayer) {
            statusMessage = "GAME OVER. Enemy Wins!";
            currentTurn = null;
        } else if (!hasEnemy) {
            statusMessage = "VICTORY! You cleared the enemies!";
            currentTurn = null;
        }
    }
}
