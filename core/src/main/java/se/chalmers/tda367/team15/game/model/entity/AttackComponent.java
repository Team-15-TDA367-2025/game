package se.chalmers.tda367.team15.game.model.entity;

public class AttackComponent {
    private final float ATTACK_DMG;
    private final int ATTACK_COOLDOWN_MS;
    private final float ATTACK_RANGE;
    private final Entity host;
    private long lastAttackTimeMS = 0;

    public AttackComponent(float attackDmg, int attackCooldownMs, float attackRange, Entity host) {
        ATTACK_DMG = attackDmg;
        ATTACK_COOLDOWN_MS = attackCooldownMs;
        ATTACK_RANGE = attackRange;
        this.host = host;
        this.lastAttackTimeMS = System.currentTimeMillis();
    }

    public void attack(HasHealth target) {
        long now = System.currentTimeMillis();
        if(now - lastAttackTimeMS < ATTACK_COOLDOWN_MS) {
            if(target.getPosition().dst(host.getPosition()) <= ATTACK_RANGE) {
                lastAttackTimeMS = System.currentTimeMillis();
                target.takeDamage(ATTACK_DMG);
            }
        }
    }
}
