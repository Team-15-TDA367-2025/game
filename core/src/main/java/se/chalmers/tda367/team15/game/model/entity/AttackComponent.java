package se.chalmers.tda367.team15.game.model.entity;

/**
 * Class that handles the attack logic of anything that can attack.
 */
public class AttackComponent {
    private final float ATTACK_DMG;
    private final int ATTACK_COOLDOWN_MS;
    private final float ATTACK_RANGE;
    private final Entity host; // we might want this to be interface 'CanAttack host'
    private long lastAttackTimeMS = 0;

    /**
     * Pass arguments to define the attack capabilities.
     *
     * @param attackDmg        the amount of damage dealt with each attack
     * @param attackCooldownMs the minimum time between attacks
     * @param attackRange      the max distance the target can be. cannot attack beyond this distance.
     * @param host             the Object that this component handles the attack logic for.
     */
    public AttackComponent(float attackDmg, int attackCooldownMs, float attackRange, Entity host) {
        ATTACK_DMG = attackDmg;
        ATTACK_COOLDOWN_MS = attackCooldownMs;
        ATTACK_RANGE = attackRange;
        this.host = host;
        this.lastAttackTimeMS = System.currentTimeMillis();
    }

    public void attack(AttackTarget target) {
        long now = System.currentTimeMillis();
        if (now - lastAttackTimeMS > ATTACK_COOLDOWN_MS) {
            if (target.hasPosition.getPosition().dst(host.getPosition()) <= ATTACK_RANGE) {
                lastAttackTimeMS = System.currentTimeMillis();
                target.canBeAttacked.takeDamage(ATTACK_DMG);
            }
        }
    }
}
