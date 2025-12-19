package se.chalmers.tda367.team15.game.model.entity.ant;

import java.util.Set;

import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

/**
 * Configuration record for ant types.
 * Defines both the egg properties (cost, time) and the ant's stats.
 * 
 * @param id                unique identifier (e.g., "worker")
 * @param displayName       human-readable name for UI
 * @param foodCost          amount of food required to create
 * @param developmentTicks  number of ticks until hatching
 * @param maxHealth         starting health of the ant
 * @param moveSpeed         movement speed of the ant
 * @param carryCapacity     resource carrying capacity
 * @param textureName       base name of the texture to use
 * @param allowedPheromones which pheromone types this ant can follow
 * @param homeBias          tendency to wander toward home (0.0 = none, 1.0 =
 *                          strong)
 */
public record AntType(
        String id,
        String displayName,
        int foodCost,
        int developmentTicks,
        float maxHealth,
        float moveSpeed,
        int carryCapacity,
        String textureName,
        Set<PheromoneType> allowedPheromones,
        float homeBias) {

            public static final class Builder {
                private String id;
                private String displayName;
                private int foodCost;
                private int developmentTicks;
                private float maxHealth;
                private float moveSpeed;
                private int carryCapacity;
                private String textureName;
                private Set<PheromoneType> allowedPheromones;
                private float homeBias;

                public Builder id(String id) {
                    this.id = id;
                    return this;
                }

                public Builder displayName(String displayName) {
                    this.displayName = displayName;
                    return this;
                }

                public Builder foodCost(int foodCost) {
                    this.foodCost = foodCost;
                    return this;
                }

                public Builder developmentTicks(int developmentTicks) {
                    this.developmentTicks = developmentTicks;
                    return this;
                }

                public Builder maxHealth(float maxHealth) {
                    this.maxHealth = maxHealth;
                    return this;
                }
                
                public Builder moveSpeed(float moveSpeed) {
                    this.moveSpeed = moveSpeed;
                    return this;
                }

                public Builder carryCapacity(int carryCapacity) {
                    this.carryCapacity = carryCapacity;
                    return this;
                }

                public Builder textureName(String textureName) {
                    this.textureName = textureName;
                    return this;
                }

                public Builder allowedPheromones(Set<PheromoneType> allowedPheromones) {
                    this.allowedPheromones = allowedPheromones;
                    return this;
                }

                public Builder homeBias(float homeBias) {
                    this.homeBias = homeBias;
                    return this;
                }

                public AntType build() {
                    return new AntType(id, displayName, foodCost, developmentTicks, maxHealth, moveSpeed, carryCapacity, textureName, allowedPheromones, homeBias);
                }
            }

            public static Builder with() {
                return new Builder();
            }
}
