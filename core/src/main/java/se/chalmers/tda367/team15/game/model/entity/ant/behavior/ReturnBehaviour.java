package se.chalmers.tda367.team15.game.model.entity.ant.behavior;

import com.badlogic.gdx.math.Vector2;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneSystem;

public class ReturnBehaviour extends AntBehavior{
    Vector2 returnHere;

    ReturnBehaviour(Ant ant, Vector2 returnHere) {
        super(ant);
        this.returnHere = returnHere;
    }

    @Override
    public void update(PheromoneSystem system, float deltaTime) {


        if(returnHere.dst(ant.getPosition()) < 0.1f) {
            ant.setBehavior(new FollowTrailBehavior(ant));
        }
        else{
            Vector2 moveV = returnHere.sub(ant.getPosition());
            ant.setVelocity(moveV.nor().scl(ant.getSpeed()));
        }

    }

}
