package Core;

public class SupportUnit extends Unit {

    public SupportUnit(String name, String symbol, int hp, int power, int moveRange, int attackRange, Team team) {
        super(name, symbol, hp, power, moveRange, attackRange, team);
    }

    public void heal(Unit target) {
        int newHp = target.getHp() + this.getPower();
        if (newHp > target.getMaxHp()) newHp = target.getMaxHp();
        target.setHp(newHp);
    }
}