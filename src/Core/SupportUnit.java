package Core;

/**
 * A unit that can heal allied units in addition to moving and attacking.
 * Extends {@link Unit} with a healing power stat.
 */
public class SupportUnit extends Unit {

    /** Amount of HP restored per heal action. */
    public int healingPower;

    /**
     * Creates a new support unit.
     *
     * @param name         the unit's display name
     * @param symbol       single-character board symbol
     * @param hp           starting (and maximum) hit points
     * @param power        attack damage dealt per attack
     * @param moveRange    maximum tiles the unit can move per turn
     * @param attackRange  maximum tiles the unit can reach when healing or attacking
     * @param team         which team this unit belongs to
     * @param healingPower HP restored per heal action
     */
    public SupportUnit(String name, String symbol, int hp, int power, int moveRange, int attackRange, Team team, int healingPower) {
        super(name, symbol, hp, power, moveRange, attackRange, team);
        this.healingPower = healingPower;
    }

    /**
     * Returns the amount of HP this unit restores per heal.
     *
     * @return healing power value
     */
    public int getHealingPower() {
        return healingPower;
    }


    /**
     * Heals a target unit by this unit's healing power, capped at the target's max HP.
     *
     * @param target the unit to heal
     */
    public void heal(Unit target) {
        int newHp = target.getHp() + getHealingPower();
        if (newHp > target.getMaxHp()) newHp = target.getMaxHp();
        target.setHp(newHp);
    }

    /**
     * Creates a copy of this support unit assigned to the given team.
     *
     * @param team the team for the cloned unit
     * @return a new {@link SupportUnit} with the same stats
     */
    @Override
    public Unit clone(Team team) {
        return new SupportUnit(this.getName(), this.getSymbol(), this.getMaxHp(),
                this.getPower(), this.getMoveRange(), this.getAttackRange(),
                team, this.healingPower);
    }
}
