package se.chalmers.tda367.team15.game.model.structure.resource;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.structure.Structure;

public class ResourceNode extends Structure {
    private final ResourceType type;
    private final int maxAmount;
    private int currentAmount;
    private final int cooldownTicks; // ticks until respawn
    private int ticksRemaining; // current countdown

    public ResourceNode(GridPoint2 position, String textureName, int radius,
                        ResourceType type, int maxAmount, int cooldownTicks) {
        super(position, textureName, radius);
        this.type = type;
        this.maxAmount = maxAmount;
        this.currentAmount = maxAmount;
        this.cooldownTicks = cooldownTicks;
        this.ticksRemaining = 0;
    }

    @Override
    public String getTextureName() {
        if (currentAmount <= 0) {
            return "grass1";
        }
        return super.getTextureName();
    }

    @Override
    public void update(float deltaTime) {
        if (currentAmount <= 0) {
            ticksRemaining--;
            if (ticksRemaining <= 0) {
                respawn();
            }
        }
    }

    public int harvest(int requestedAmount) {
        if (currentAmount <= 0)
            return 0;

        int harvestedAmount = Math.min(currentAmount, requestedAmount);
        currentAmount -= harvestedAmount;

        if (currentAmount <= 0) {
            ticksRemaining = cooldownTicks;
        }

        return harvestedAmount;
    }

    private void respawn() {
        currentAmount = maxAmount;
        ticksRemaining = 0;
    }

    public ResourceType getType() {
        return type;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }
}
