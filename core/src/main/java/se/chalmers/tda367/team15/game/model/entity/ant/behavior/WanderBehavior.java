package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.Pheromone;
import se.chalmers.tda367.team15.game.model.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;

public class WanderBehavior implements AntBehavior {
    
    @Override
    public void update(Ant ant, PheromoneSystem system, float deltaTime) {
        // Default wander movement
        ant.move(ant.getVelocity().cpy().scl(deltaTime));
        ant.updateRotation();

        // Check for pheromones to switch behavior
        if (system != null) {
            GridPoint2 gridPos = ant.getGridPosition();
            List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos);

            if (!neighbors.isEmpty()) {
                ant.setBehavior(new FollowTrailBehavior());
            }
        }
    }
}

