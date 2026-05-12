package Core;

public class SupportUnit extends Unit {
    public int healingPower;
    public SupportUnit(String name, String symbol, int hp, int power, int moveRange, int attackRange, Team team, int healingPower) {
        super(name, symbol, hp, power, moveRange, attackRange, team);
        this.healingPower = healingPower;
    }

    public int getHealingPower() {
        return healingPower;
    }

    public void heal(Unit target) {
        int newHp = target.getHp() + getHealingPower();
        if (newHp > target.getMaxHp()) newHp = target.getMaxHp();
        target.setHp(newHp);
    }

    @Override
    public Unit clone(Team team) {
        return new SupportUnit(this.getName(), this.getSymbol(), this.getMaxHp(),
                this.getPower(), this.getMoveRange(), this.getAttackRange(),
                team, this.healingPower);
    }
}