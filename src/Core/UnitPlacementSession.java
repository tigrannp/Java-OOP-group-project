package Core;

import java.util.ArrayList;

public class UnitPlacementSession {
    private ArrayList<Unit> templates;
    private ArrayList<Unit> placedUnits;

    private static final int MAX_TOTAL = 5;
    private static final int MAX_KNIGHTS = 3;
    private static final int MAX_ARCHERS = 3;
    private static final int MAX_CLERICS = 2;
    private static final int MAX_COL = 5;
    private static final int ROWS = 7;

    public UnitPlacementSession(ArrayList<Unit> templates) {
        this.templates = templates;
        this.placedUnits = new ArrayList<Unit>();
    }

    public ArrayList<Unit> getPlacedUnits() { return placedUnits; }
    public int getTotalPlaced() { return placedUnits.size(); }
    public boolean isReady() { return placedUnits.size() > 0; }

    public ArrayList<Unit> getTemplates() { return templates; }

    public int countByName(String name) {
        int count = 0;
        for (Unit u : placedUnits) {
            if (u.getName().equals(name)) count++;
        }
        return count;
    }

    public boolean isTileTaken(int row, int col) {
        for (Unit u : placedUnits) {
            if (u.getRow() == row && u.getCol() == col) return true;
        }
        return false;
    }

    public PlacementResult placeUnit(String name, int row, int col) {
        if (placedUnits.size() >= MAX_TOTAL)
            return PlacementResult.EXCEED_TOTAL;
        if (row < 0 || row >= ROWS || col < 0 || col > MAX_COL)
            return PlacementResult.OUT_OF_BOUNDS;
        if (isTileTaken(row, col))
            return PlacementResult.TILE_TAKEN;

        if (name.equals("Knight") && countByName("Knight") >= MAX_KNIGHTS)
            return PlacementResult.EXCEED_TYPE;
        if (name.equals("Archer") && countByName("Archer") >= MAX_ARCHERS)
            return PlacementResult.EXCEED_TYPE;
        if (name.equals("Cleric") && countByName("Cleric") >= MAX_CLERICS)
            return PlacementResult.EXCEED_TYPE;

        Unit template = getTemplate(name);
        if (template == null) return PlacementResult.INVALID_TYPE;

        Unit unit;
        if (template instanceof SupportUnit) {
            unit = new SupportUnit(template.getName(), template.getSymbol(), template.getMaxHp(),
                    template.getPower(), template.getMoveRange(), template.getAttackRange(),
                    Team.PLAYER, ((SupportUnit) template).getHealingPower());
        } else {
            unit = new Unit(template.getName(), template.getSymbol(), template.getMaxHp(),
                    template.getPower(), template.getMoveRange(), template.getAttackRange(),
                    Team.PLAYER);
        }

        unit.setRow(row);
        unit.setCol(col);
        placedUnits.add(unit);
        return PlacementResult.SUCCESS;
    }

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

    private Unit getTemplate(String name) {
        for (Unit t : templates) {
            if (t.getName().equals(name)) return t;
        }
        return null;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Placed units (").append(placedUnits.size()).append("/").append(MAX_TOTAL).append("):\n");
        for (Unit u : placedUnits) {
            sb.append("  ").append(u.getName()).append(" at (").append(u.getRow()).append(", ").append(u.getCol()).append(")\n");
        }
        sb.append("Remaining slots: Knights ").append(MAX_KNIGHTS - countByName("Knight"))
                .append(", Archers ").append(MAX_ARCHERS - countByName("Archer"))
                .append(", Clerics ").append(MAX_CLERICS - countByName("Cleric"));
        return sb.toString();
    }
}