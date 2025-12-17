package se.chalmers.tda367.team15.game.model.entity.termite;

public class TermiteBehaviourManager {
    TermiteAttackBehaviour attackBehaviour;
    public TermiteBehaviourManager(TermiteAttackBehaviour attackBehaviour) {
        this.attackBehaviour=attackBehaviour;
    }

    public void update() {
        attackBehaviour.update();
    }
}
