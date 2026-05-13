package Core;

import java.util.ArrayList;

/**
 * Manages the pre-game unit placement phase.
 * Enforces placement limits and tracks which units the player has placed on the board.
 */
public class UnitPlacementSession {
    private ArrayList<Unit> templates;
    private ArrayList<Unit> placedUnits;

    private static final int MAX_TOTAL = 5;
    private static final int MAX_UNITS = 3;
    private static final int MAX_COL = 5;
    private static final int ROWS = 7;

    /**
     * Creates a new placement session with the given unit templates.
     *
     * @param templates the available unit types the player can choose from
     */
    public UnitPlacementSession(ArrayList<Unit> templates) {
        this.templates = templates;
        this.placedUnits = new ArrayList<Unit>();
    }

    /**
     * Returns the list of units placed by the player so far.
     *
     * @return list of placed units
     */
    public ArrayList<Unit> getPlacedUnits() { return placedUnits; }

    /**
     * Returns the number of units placed so far.
     *
     * @return total placed unit count
     */
    public int getTotalPlaced() { return placedUnits.size(); }

    /**
     * Returns whether the player has placed at least one unit.
     *
     * @return true if ready to start the game
     */
    public boolean isReady() { return placedUnits.size() > 0; }

    /**
     * Returns the list of available unit templates.
     *
     * @return unit templates
     */
    public ArrayList<Unit> getTemplates() { return templates; }

    /**
     * Counts how many placed units have the given name.
     *
     * @param name the unit type name to count
     * @return number of placed units with that name
     */
    public int countByName(String name) {
        int count = 0;
        for (Unit u : placedUnits) {
            if (u.getName().equals(name)) count++;
        }
        return count;
    }

    /**
     * Checks whether a tile is already occupied by a placed unit.
     *
     * @param row the row to check
     * @param col the column to check
     * @return true if the tile is taken
     */
    public boolean isTileTaken(int row, int col) {
        for (Unit u : placedUnits) {
            if (u.getRow() == row && u.getCol() == col) return true;
        }
        return false;
    }

    /**
     * Attempts to place a unit of the given type at the specified position.
     *
     * @param name the unit type name (e.g. "Knight", "Archer", "Cleric")
     * @param row  the target row
     * @param col  the target column (must be 0–5)
     * @return a {@link PlacementResult} indicating success or the reason for failure
     */
    public PlacementResult placeUnit(String name, int row, int col) {
        if (placedUnits.size() >= MAX_TOTAL)
            return PlacementResult.EXCEED_TOTAL;
        if (row < 0 || row >= ROWS || col < 0 || col > MAX_COL)
            return PlacementResult.OUT_OF_BOUNDS;
        if (isTileTaken(row, col))
            return PlacementResult.TILE_TAKEN;

        if (name.equals("Knight") && countByName("Knight") >= MAX_UNITS)
            return PlacementResult.EXCEED_TYPE;
        if (name.equals("Archer") && countByName("Archer") >= MAX_UNITS)
            return PlacementResult.EXCEED_TYPE;
        if (name.equals("Cleric") && countByName("Cleric") >= MAX_UNITS)
            return PlacementResult.EXCEED_TYPE;

        Unit template = getTemplate(name);
        if (template == null) return PlacementResult.INVALID_TYPE;

        Unit unit = template.clone(Team.PLAYER);

        unit.setRow(row);
        unit.setCol(col);
        placedUnits.add(unit);
        return PlacementResult.SUCCESS;
    }

    /**
     * Removes the unit at the specified position, if any.
     *
     * @param row the row of the unit to remove
     * @param col the column of the unit to remove
     * @return true if a unit was removed, false if the tile was empty
     */
    public boolean removeUnit(int row, int col) {
        for (int i = 0; i < placedUnits.size(); i++) {
            Unit u = placedUnits.get(i);
            if (u.getRow() == row && u.getCol() == col) {
                placedUnits.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a summary of currently placed units and remaining slots.
     *
     * @return formatted summary string
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Placed units (").append(placedUnits.size()).append("/").append(MAX_TOTAL).append("):\n");
        for (Unit u : placedUnits) {
            sb.append("  ").append(u.getName()).append(" at (").append(u.getRow()).append(", ").append(u.getCol()).append(")\n");
        }
        sb.append("Remaining slots: Knights ").append(MAX_UNITS - countByName("Knight"))
                .append(", Archers ").append(MAX_UNITS - countByName("Archer"))
                .append(", Clerics ").append(MAX_UNITS - countByName("Cleric"));
        return sb.toString();
    }

    private Unit getTemplate(String name) {
        for (Unit t : templates) {
            if (t.getName().equals(name)) return t;
        }
        return null;
    }
}
