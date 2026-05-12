package Core;

import java.util.ArrayList;

public class GameEngine {
    private ArrayList<Unit> units;
    private Unit selectedUnit;
    private Team currentTurn;
    private String statusMessage;

    public GameEngine() {
        this.units = new ArrayList<Unit>();
        this.currentTurn = Team.PLAYER;
        this.statusMessage = "Welcome! Player's turn. Your units are blue.";
        setupGame();
    }

    private void setupGame() {
        addUnit(new Unit("Knight", "K", 30, 10, 2, 1, Team.PLAYER), 2, 1);
        addUnit(new Unit("Knight", "K", 30, 10, 2, 1, Team.PLAYER), 4, 1);
        addUnit(new Unit("Archer", "A", 20, 8, 2, 3, Team.PLAYER), 1, 0);
        addUnit(new Unit("Archer", "A", 20, 8, 2, 3, Team.PLAYER), 5, 0);
        addUnit(new SupportUnit("Cleric", "C", 20, 5, 2, 2, Team.PLAYER), 3, 0);

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
    public ArrayList<Unit> getUnits() { return this.units; }
    public Unit getSelectedUnit() { return this.selectedUnit; }
    public Team getCurrentTurn() { return this.currentTurn; }
    public String getStatusMessage() { return this.statusMessage; }
    public Unit getUnitAt(int r, int c) {
        for (int i = 0; i < units.size(); i++) {
            Unit u = units.get(i);
            if (u.getRow() == r && u.getCol() == c) return u;
        }
        return null;
    }

    private int getDistance(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    public void handleClick(int r, int c) {
        Unit clickedUnit = getUnitAt(r, c);

        if (clickedUnit != null && clickedUnit.getTeam() == Team.PLAYER) {
            if (selectedUnit != null && selectedUnit instanceof SupportUnit && clickedUnit != selectedUnit) {
                int dist = getDistance(selectedUnit.getRow(), selectedUnit.getCol(), r, c);
                if (dist <= selectedUnit.getAttackRange() && !selectedUnit.getHasActed()) {
                    ((SupportUnit) selectedUnit).heal(clickedUnit);
                    selectedUnit.setHasActed(true);
                    statusMessage = "Healed " + clickedUnit.getName() + "!";
                    selectedUnit = null;
                } else {
                    selectedUnit = clickedUnit;
                }
            } else {
                selectedUnit = clickedUnit;
                statusMessage = "Selected " + selectedUnit.getName();
            }
        } else if (selectedUnit != null && selectedUnit.getTeam() == Team.PLAYER) {
            int dist = getDistance(selectedUnit.getRow(), selectedUnit.getCol(), r, c);

            if (clickedUnit == null) {
                if (dist <= selectedUnit.getMoveRange() && !selectedUnit.getHasMoved()) {
                    selectedUnit.setRow(r);
                    selectedUnit.setCol(c);
                    selectedUnit.setHasMoved(true);
                    statusMessage = selectedUnit.getName() + " moved.";
                } else {
                    selectedUnit = null;
                }
            } else if (clickedUnit.getTeam() == Team.ENEMY) {
                if (dist <= selectedUnit.getAttackRange() && !selectedUnit.getHasActed()) {
                    clickedUnit.setHp(clickedUnit.getHp() - selectedUnit.getPower());
                    selectedUnit.setHasActed(true);
                    statusMessage = "Attacked " + clickedUnit.getName() + "!";
                    if (clickedUnit.getHp() <= 0) {
                        units.remove(clickedUnit);
                        statusMessage = clickedUnit.getName() + " destroyed!";
                    }
                    selectedUnit = null;
                } else {
                    selectedUnit = null;
                }
            }
        }
        checkWinCondition();
    }

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