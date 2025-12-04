package se.chalmers.tda367.team15.game.model.structure.resource;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;
import se.chalmers.tda367.team15.game.model.structure.Structure;

public class ResourceNode extends Structure implements TimeObserver {
    private ResourceType type;
    private int amount; // Total amount of resource
    private int currentAmount; // Current available amount
    private float coolDownTime; // Time to regenerate resource
    private float coolDownTimer; // Timer for regeneration
    private boolean depleted;

    public ResourceNode(GridPoint2 position, String textureName, int radius, ResourceType type, int amount,
            float coolDownTime) {
        super(position, textureName, radius);
        this.type = type;
        this.amount = amount;
        this.currentAmount = amount;
        this.coolDownTime = coolDownTime;
        this.coolDownTimer = 0;
        this.depleted = false;
    }

    // @Override
    // public String getTextureName() {
    // if (depleted) {
    // return null; // null = won't render
    // }
    // return "Resource";
    // }

    public void onTick() {
        System.out.println(currentAmount);
        if (depleted) {
            coolDownTimer += 1;
            if (coolDownTimer >= coolDownTime) {
                respawn();
            }
        }
    }

    public int harvest(int harvestAmount) {
        if (depleted) {
            return 0;
        }
        int harvested = Math.min(harvestAmount, currentAmount);
        currentAmount -= harvested;
        if (currentAmount <= 0) {
            depleted = true;
            currentAmount = 0;
        }
        return harvested;
    }

    public void respawn() {
        currentAmount = amount;
        depleted = false;
        coolDownTimer = 0;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public boolean isDepleted() {
        return depleted;
    }

    public ResourceType getResourceType() {
        return type;
    }
    // Getters and setters
}
