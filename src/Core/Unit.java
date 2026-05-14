package Core;
import Exceptions.InvalidUnitException;

/**
 * Represents a game unit with stats, position, and turn state.
 */
public class Unit implements Cloneable {
    private String name;
    private String symbol;
    private int hp;
    private int maxHp;
    private int power;
    private int moveRange;
    private int attackRange;
    private int row;
    private int col;
    private boolean hasMoved;
    private boolean hasActed;
    private Team team;

    /**
     * Creates a new unit with the given stats.
     *
     * @param name        the unit's display name
     * @param symbol      single-character board symbol
     * @param hp          starting (and maximum) hit points
     * @param power       attack damage dealt per attack
     * @param moveRange   maximum tiles the unit can move per turn
     * @param attackRange maximum tiles the unit can reach when attacking
     * @param team        which team this unit belongs to
     */
    public Unit(String name, String symbol, int hp, int power, int moveRange, int attackRange, Team team) {
        this.name = name;
        this.symbol = symbol;
        this.hp = hp;
        this.maxHp = hp;
        this.power = power;
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.hasMoved = false;
        this.hasActed = false;
        this.team = team;
    }

    /**
     * Validates that this unit's position is not negative.
     *
     * @throws InvalidUnitException if row or col is negative
     */
    public void validatePosition() throws InvalidUnitException {
        if (this.row < 0 || this.col < 0) {
            throw new InvalidUnitException("Unit position cannot be negative.");
        }
    }

    /** @return the unit's name */
    public String getName() { return this.name; }

    /** @return the unit's board symbol */
    public String getSymbol() { return this.symbol; }

    /** @return current hit points */
    public int getHp() { return this.hp; }

    /** @return maximum hit points */
    public int getMaxHp() { return this.maxHp; }

    /** @return attack power */
    public int getPower() { return this.power; }

    /** @return movement range in tiles */
    public int getMoveRange() { return this.moveRange; }

    /** @return attack range in tiles */
    public int getAttackRange() { return this.attackRange; }

    /** @return the team this unit belongs to */
    public Team getTeam() { return this.team; }

    /** @return current row on the board */
    public int getRow() { return this.row; }

    /** @return current column on the board */
    public int getCol() { return this.col; }

    /** @return true if the unit has already moved this turn */
    public boolean getHasMoved() { return this.hasMoved; }

    /** @return true if the unit has already acted this turn */
    public boolean getHasActed() { return this.hasActed; }

    /**
     * Sets the unit's row position.
     *
     * @param row the new row
     */
    public void setRow(int row) { this.row = row; }

    /**
     * Sets the unit's column position.
     *
     * @param col the new column
     */
    public void setCol(int col) { this.col = col; }

    /**
     * Marks whether the unit has moved this turn.
     *
     * @param moved true if the unit has moved
     */
    public void setHasMoved(boolean moved) { this.hasMoved = moved; }

    /**
     * Marks whether the unit has acted this turn.
     *
     * @param acted true if the unit has acted
     */
    public void setHasActed(boolean acted) { this.hasActed = acted; }

    /**
     * Sets the unit's current hit points.
     *
     * @param hp the new HP value
     */
    public void setHp(int hp) { this.hp = hp; }

    /**
     * Moves the unit to a new position and marks it as having moved.
     *
     * @param newR the destination row
     * @param newC the destination column
     */
    public void move(int newR, int newC) {
        this.row = newR;
        this.col = newC;
        this.hasMoved = true;
    }

    /**
     * Attacks a target unit, reducing its HP by this unit's power.
     * Marks this unit as having acted.
     *
     * @param target the unit to attack
     */
    public void attack(Unit target) {
        int newHp = target.getHp() - this.getPower();
        target.setHp(newHp);
        this.hasActed = true;
    }

    /**
     * Creates a shallow copy of this unit, preserving all stats and team.
     *
     * @return a cloned {@link Unit}, or null if cloning fails
     */
    public Unit clone() {
        try {
            return (Unit) super.clone();
        } catch (CloneNotSupportedException e) {

        }
        return null;
    }
}
