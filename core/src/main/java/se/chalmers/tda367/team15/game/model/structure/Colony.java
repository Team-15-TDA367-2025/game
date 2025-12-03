package se.chalmers.tda367.team15.game.model.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.AttackCategory;
import se.chalmers.tda367.team15.game.model.CanBeAttacked;
import se.chalmers.tda367.team15.game.model.DestructionListener;
import se.chalmers.tda367.team15.game.model.entity.Entity;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.faction.Faction;

public class Colony extends Structure implements CanBeAttacked {
    private List<Ant> ants;

    private float health;
    private float MAX_HEALTH = 60;
    public Colony(GridPoint2 position) {
        super(position, "AntColony", 5);
        this.ants = new ArrayList<>();
        faction= Faction.DEMOCRATIC_REPUBLIC_OF_ANTS;
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public void removeAnt(Ant ant) {
        ants.remove(ant);
    }

    @Override
    public void update(float deltaTime) {
        for (Ant ant : ants) {
            ant.update(deltaTime);
        }
    }
    @Override
    public Collection<Entity> getSubEntities() {
        return Collections.unmodifiableCollection(ants);
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public void takeDamage(float amount) {
        health = Math.max(0f,health-amount);
        if(health == 0f) {
            die();
        }
    }

    @Override
    public void die() {
        DestructionListener.getInstance().notifyStructureDeathObservers(this);
    }

    @Override
    public AttackCategory getAttackCategory() {
        return AttackCategory.ANT_COLONY;
    }
}
