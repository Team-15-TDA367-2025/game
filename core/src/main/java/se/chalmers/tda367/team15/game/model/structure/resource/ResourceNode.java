package se.chalmers.tda367.team15.game.model.structure.resource;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.structure.Structure;

public class ResourceNode extends Structure {
    private ResourceType type;
    private int maxAmount;
    private int currentAmount;
    private int cooldownTicks; // ticks until respawn
    private int ticksRemaining; // current countdown
    private boolean depleted;

    public ResourceNode(GridPoint2 position, String textureName, int radius,
                        ResourceType type, int maxAmount, int cooldownTicks) {
        super(position, textureName, radius);
        this.type = type;
        this.maxAmount = maxAmount;
        this.currentAmount = maxAmount;
        this.cooldownTicks = cooldownTicks;
        this.ticksRemaining = 0;
        this.depleted = false;
    }

    @Override
    public String getTextureName() {
        if (depleted) {
            return "grass1";
        }
        return super.getTextureName();
    }

    @Override
    public void update(float deltaTime) {
        if (depleted) {
            ticksRemaining--;
            if (ticksRemaining <= 0) {
                respawn();
            }
        }
    }

    public int harvest(int requestedAmount) {
        if (depleted)
            return 0;

        int harvestedAmount = Math.min(currentAmount, requestedAmount);
        currentAmount -= harvestedAmount;

        if (currentAmount <= 0) {
            depleted = true;
            ticksRemaining = cooldownTicks;
        }

        return harvestedAmount;
    }

    private void respawn() {
        currentAmount = maxAmount;
        depleted = false;
        ticksRemaining = 0;
    }

    public boolean isDepleted() {
        return depleted;
    }

    public ResourceType getType() {
        return type;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }
}
