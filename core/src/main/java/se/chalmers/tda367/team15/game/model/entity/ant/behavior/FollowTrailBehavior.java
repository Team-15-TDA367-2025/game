package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import se.chalmers.tda367.team15.game.model.Pheromone;
import se.chalmers.tda367.team15.game.model.PheromoneSystem;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;

public class FollowTrailBehavior implements AntBehavior {
    private boolean returningToColony = false;
    private static final float TURN_RADIUS = 1.0f;
    private static final float SPEED_BOOST_ON_TRAIL = 10f;

    @Override
    public void update(Ant ant, PheromoneSystem system, float deltaTime) {
        GridPoint2 gridPos = ant.getGridPosition();
        List<Pheromone> neighbors = system.getPheromonesIn3x3(gridPos);

        if (neighbors.isEmpty()) {
            ant.setBehavior(new WanderBehavior());
            return;
        }

        // Find where we are on the trail (closest pheromone)
        Pheromone closest = getClosestPheromone(ant, neighbors);
        if (closest == null) return; // Should not happen if neighbors not empty
        
        int currentDist = closest.getDistance();

        Vector2 targetPos = null;

        if (returningToColony) {
            // Try to go closer to colony (any distance < currentDist)
            targetPos = findLowerDistancePosition(neighbors, currentDist);

            // If no lower dist, check if we are at the start (dist 1)
            if (targetPos == null) {
                if (currentDist <= 1) {
                    // We are next to colony. Move towards colony center.
                    GridPoint2 colonyGridPos = system.getColonyPosition();
                    Vector2 colonyCenter = new Vector2(colonyGridPos.x + 0.5f, colonyGridPos.y + 0.5f);
                    if (ant.getPosition().dst(colonyCenter) < TURN_RADIUS) {
                        returningToColony = false; // Turn around
                    } else {
                        targetPos = colonyCenter;
                    }
                } else {
                    // Stuck in local minimum? Turn around.
                    returningToColony = false; 
                }
            }
        } 
        
        if (!returningToColony) {
            // Try to go away from colony (any distance > currentDist)
            targetPos = findHigherDistancePosition(neighbors, currentDist);
            
            // Special case: Leaving colony (at dist 0 theoretically, but closest is 1).
            // If we can't see any higher distance, but we are at 1, we might be at the colony looking at 1.
            if (targetPos == null && currentDist == 1) {
                 // Check if we are NOT on top of P1 yet
                 GridPoint2 p1GridPos = closest.getPosition();
                 Vector2 p1WorldPos = new Vector2(p1GridPos.x + 0.5f, p1GridPos.y + 0.5f);
                 float distToP1 = ant.getPosition().dst(p1WorldPos);
                 if (distToP1 > 0.5f) {
                     // We are likely at colony, trying to reach P1.
                     targetPos = p1WorldPos;
                 }
            }

            // If still no target, check if end of trail
            if (targetPos == null) {
                // Verify no higher distance exists at all (reached tip)
                boolean hasHigher = false;
                for(Pheromone p : neighbors) {
                    if(p.getDistance() > currentDist) {
                        hasHigher = true;
                        break;
                    }
                }
                
                if (!hasHigher) {
                    returningToColony = true; // Turn around
                }
            }
        }

        if (targetPos != null) {
            seek(ant, targetPos, deltaTime);
        } else {
            // If waiting for turn around or stuck, orbit closest to stay on trail
            GridPoint2 closestGridPos = closest.getPosition();
            Vector2 closestWorldPos = new Vector2(closestGridPos.x + 0.5f, closestGridPos.y + 0.5f);
            seek(ant, closestWorldPos, deltaTime);
        }
        
        ant.updateRotation();
    }

    private Vector2 findHigherDistancePosition(List<Pheromone> neighbors, int currentDist) {
        List<Pheromone> candidates = new ArrayList<>();
        for (Pheromone p : neighbors) {
            if (p.getDistance() > currentDist) {
                candidates.add(p);
            }
        }
        if (candidates.isEmpty()) return null;
        
        // Uniformly choose randomly from all valid candidates with higher distance
        Pheromone chosen = candidates.get(MathUtils.random.nextInt(candidates.size()));
        GridPoint2 chosenGridPos = chosen.getPosition();
        return new Vector2(chosenGridPos.x + 0.5f, chosenGridPos.y + 0.5f);
    }
    
    private Vector2 findLowerDistancePosition(List<Pheromone> neighbors, int currentDist) {
        List<Pheromone> candidates = new ArrayList<>();
        for (Pheromone p : neighbors) {
            if (p.getDistance() < currentDist) {
                candidates.add(p);
            }
        }
        if (candidates.isEmpty()) return null;
        
        // Uniformly choose randomly from all valid candidates with lower distance
        Pheromone chosen = candidates.get(MathUtils.random.nextInt(candidates.size()));
        GridPoint2 chosenGridPos = chosen.getPosition();
        return new Vector2(chosenGridPos.x + 0.5f, chosenGridPos.y + 0.5f);
    }
    

    private Pheromone getClosestPheromone(Ant ant, List<Pheromone> pheromones) {
        Pheromone closest = null;
        float minDst = Float.MAX_VALUE;
        Vector2 antPos = ant.getPosition();
        for (Pheromone p : pheromones) {
            GridPoint2 pGridPos = p.getPosition();
            Vector2 pWorldPos = new Vector2(pGridPos.x + 0.5f, pGridPos.y + 0.5f);
            float dst = antPos.dst2(pWorldPos);
            if (dst < minDst) {
                minDst = dst;
                closest = p;
            }
        }
        return closest;
    }

    private void seek(Ant ant, Vector2 target, float deltaTime) {
        Vector2 pos = ant.getPosition();
        Vector2 dir = target.cpy().sub(pos);
        if (dir.len2() > 0.001f) {
            dir.nor().scl(ant.getSpeed() * SPEED_BOOST_ON_TRAIL);
            ant.setVelocity(dir); // Precise control
            ant.move(ant.getVelocity().cpy().scl(deltaTime));
        }
    }
}
