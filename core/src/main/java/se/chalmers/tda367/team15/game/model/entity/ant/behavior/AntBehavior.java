package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.enemy.Termite;
import se.chalmers.tda367.team15.game.model.interfaces.EntityQuery;
import se.chalmers.tda367.team15.game.model.interfaces.providers.PheromoneUsageProvider;

/**
 * Used to update the ants, the ants have a specific behaviour programmed. The
 * behaviour is a state machine, behaviour can be unchanged or switch each
 * update.
 * When a behaviour will switch the ant's behaviour or leave it unchanged is
 * controlled based on internal logic of the type of behaviour.
 */
public abstract class AntBehavior implements GeneralizedBehaviour {
    protected final Ant ant;
    protected final EntityQuery entityQuery;

    public AntBehavior(Ant ant, EntityQuery entityQuery) {
        this.ant = ant;
        this.entityQuery = entityQuery;
    }

    public boolean enemiesInSight() {
        List<? extends Termite> entities = entityQuery.getEntitiesOfType(Termite.class);

        Vector2 antPosition = ant.getPosition();
        float visionRadiusSq = ant.getVisionRadius() * ant.getVisionRadius();

        for (Termite entity : entities) {
            if (entity.getFaction().equals(ant.getFaction())) {
                continue;
            }

            if (entity.getPosition().dst2(antPosition) <= visionRadiusSq) {
                return true;
            }
        }

        return false;
    }

    public abstract void update(PheromoneUsageProvider system);

    public void handleCollision() {
    }
}
