package se.chalmers.tda367.team15.game.model.egg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import se.chalmers.tda367.team15.game.model.TimeCycle;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.interfaces.TimeObserver;

/**
 * Manages the collection of eggs and their development lifecycle.
 * Implements TimeObserver to tick eggs on game time updates.
 */
public class EggManager implements TimeObserver {
    private final List<Egg> eggs;
    private final List<EggHatchObserver> listeners;

    public EggManager() {
        this.eggs = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void addEgg(AntType type) {
        Egg egg = new Egg(type.id(), type.developmentTicks());
        eggs.add(egg);
    }

    public void addObserver(EggHatchObserver listener) {
        listeners.add(listener);
    }

    public void removeObserver(EggHatchObserver listener) {
        listeners.remove(listener);
    }

    @Override
    public void onTimeUpdate(TimeCycle timeCycle) {
        for (Egg egg : eggs) {
            egg.tick();

            if (egg.isHatched()) {
                AntType type = egg.getType();
                if (type == null) {
                    continue;
                }

                for (EggHatchObserver listener : listeners) {
                    listener.onEggHatch(type);
                }
            }
        }
        eggs.removeIf(Egg::isHatched);
    }

    public List<Egg> getEggs() {
        return Collections.unmodifiableList(eggs);
    }
}
