package se.chalmers.tda367.team15.game.model.egg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;

/**
 * Manages the collection of eggs and their development lifecycle.
 * Implements TimeObserver to tick eggs on game time updates.
 */
public class EggManager implements TimeObserver {
    private final List<Egg> eggs;
    private final List<EggHatchObserver> observers;
    private final AntTypeRegistry antTypeRegistry;
    // TODO: Do we really need this here?
    private final AntFactory antFactory;

    public EggManager(AntTypeRegistry antTypeRegistry, AntFactory antFactory) {
        this.antTypeRegistry = antTypeRegistry;
        this.antFactory = antFactory;
        this.eggs = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addEgg(AntType type) {
        Egg egg = new Egg(type.id(), type.developmentTicks(), antTypeRegistry);
        eggs.add(egg);
    }

    public void addObserver(EggHatchObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(EggHatchObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void onMinute() {
        for (Egg egg : eggs) {
            egg.tick();
            if (!egg.isHatched()) {
                continue;
            }

            AntType type = egg.getType();

            if (type == null) {
                throw new IllegalArgumentException("Egg type is null");
            }

            observers.forEach(observer -> observer.onEggHatch(antFactory, type));
        }
        eggs.removeIf(Egg::isHatched);
    }

    public List<Egg> getEggs() {
        return Collections.unmodifiableList(eggs);
    }
}
