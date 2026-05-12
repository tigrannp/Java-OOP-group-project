package Core;
import Exceptions.InvalidUnitException;

public class Unit {
    private String name;
    private String symbol;
    private int hp;
    private int maxHp;
    private int power;
    private int moveRange;
    private int attackRange;
    private boolean isSupport;
    private int row;
    private int col;
    private boolean hasMoved;
    private boolean hasActed;
    private Team team;

    public Unit(String name, String symbol, int hp, int power, int moveRange, int attackRange, boolean isSupport, Team team) {
        this.name = name;
        this.symbol = symbol;
        this.hp = hp;
        this.maxHp = hp;
        this.power = power;
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.isSupport = isSupport;
        this.hasMoved = false;
        this.hasActed = false;
        this.team = team;
    }

    public void validatePosition() throws InvalidUnitException {
        if (this.row < 0 || this.col < 0) {
            throw new InvalidUnitException("Unit position cannot be negative.");
        }
    }

    // Getters
    public String getName() { return this.name; }
    public String getSymbol() { return this.symbol; }
    public int getHp() { return this.hp; }
    public int getMaxHp() { return this.maxHp; }
    public int getPower() { return this.power; }
    public int getMoveRange() { return this.moveRange; }
    public int getAttackRange() { return this.attackRange; }
    public boolean getIsSupport() { return this.isSupport; }
    public Team getTeam() { return this.team; }
    public int getRow() { return this.row; }
    public int getCol() { return this.col; }
    public boolean getHasMoved() { return this.hasMoved; }
    public boolean getHasActed() { return this.hasActed; }

    // Setters
    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }
    public void setHasMoved(boolean moved) { this.hasMoved = moved; }
    public void setHasActed(boolean acted) { this.hasActed = acted; }
    public void setHp(int hp) {
        this.hp = hp;
    }
}