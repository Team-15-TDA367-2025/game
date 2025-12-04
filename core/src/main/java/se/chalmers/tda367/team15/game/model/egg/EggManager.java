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
    private final List<EggHatchListener> listeners;

    public EggManager() {
        this.eggs = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * Adds a new egg to the manager.
     *
     * @param type the type of egg to add
     */
    public void addEgg(AntType type) {
        Egg egg = new Egg(type.id(), type.developmentTicks());
        eggs.add(egg);
    }

    /**
     * Adds an egg hatch listener.
     *
     * @param listener the listener to add
     */
    public void addListener(EggHatchListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes an egg hatch listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(EggHatchListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onTimeUpdate(TimeCycle timeCycle) {
        Iterator<Egg> iterator = eggs.iterator();
        while (iterator.hasNext()) {
            Egg egg = iterator.next();
            egg.tick();

            if (egg.isHatched()) {
                AntType type = egg.getType();
                if (type != null) {
                    // Notify all listeners
                    for (EggHatchListener listener : listeners) {
                        listener.onEggHatch(type);
                    }
                }
                iterator.remove();
            }
        }
    }

    /**
     * Gets a read-only view of all eggs.
     * Used by the view layer to display egg progress.
     *
     * @return unmodifiable list of eggs
     */
    public List<Egg> getEggs() {
        return Collections.unmodifiableList(eggs);
    }

    /**
     * Gets the number of eggs currently in development.
     *
     * @return the egg count
     */
    public int getEggCount() {
        return eggs.size();
    }
}
