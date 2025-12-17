package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.managers.PheromoneManager;

public abstract class AntBehavior {
    protected Ant ant;
    protected EntityQuery entityQuery;

    public AntBehavior(Ant ant, EntityQuery entityQuery) {
        this.ant = ant;
        this.entityQuery = entityQuery;
    }

    public boolean enemiesInSight() {
        List<CanBeAttacked> entities = entityQuery.getEntitiesOfType(CanBeAttacked.class);
        entities.removeIf(e -> e.getFaction() == ant.getFaction());
        
        Vector2 antPosition = ant.getPosition();
        float visionRadiusSq = ant.getVisionRadius() * ant.getVisionRadius();
        
        for (CanBeAttacked entity : entities) {
            if (entity.getPosition().dst2(antPosition) <= visionRadiusSq) {
                return true;
            }
        }
        
        return false;
    }

    public abstract void update(PheromoneManager system);


    public void handleCollision() {}
}
