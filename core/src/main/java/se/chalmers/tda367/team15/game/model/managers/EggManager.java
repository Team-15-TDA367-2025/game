package se.chalmers.tda367.team15.game.model.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import se.chalmers.tda367.team15.game.model.AntFactory;
import se.chalmers.tda367.team15.game.model.Egg;
import se.chalmers.tda367.team15.game.model.entity.ant.Ant;
import se.chalmers.tda367.team15.game.model.entity.ant.AntType;
import se.chalmers.tda367.team15.game.model.entity.ant.AntTypeRegistry;
import se.chalmers.tda367.team15.game.model.interfaces.Home;
import se.chalmers.tda367.team15.game.model.interfaces.observers.TimeObserver;
import se.chalmers.tda367.team15.game.model.interfaces.providers.EggPurchaseProvider;
import se.chalmers.tda367.team15.game.model.interfaces.providers.EntityModificationProvider;
import se.chalmers.tda367.team15.game.model.structure.resource.ResourceType;

/**
 * Manages the collection of eggs and their development lifecycle.
 * Implements TimeObserver to tick eggs on game time updates.
 */
// TODO: Should the egg manager be in /egg not in /managers?
public class EggManager implements TimeObserver, EggPurchaseProvider {
    private final List<Egg> eggs;
    private final AntTypeRegistry antTypeRegistry;
    // TODO: Do we really need this here?
    private final AntFactory antFactory;
    private final Home home;
    private final EntityModificationProvider entityManager;

    public EggManager(AntTypeRegistry antTypeRegistry, AntFactory antFactory, Home home,
            EntityModificationProvider entityManager) {
        this.antTypeRegistry = antTypeRegistry;
        this.antFactory = antFactory;
        this.home = home;
        this.eggs = new ArrayList<>();
        this.entityManager = entityManager;
    }

    public void addEgg(AntType type) {
        Egg egg = new Egg(type.id(), type.developmentTicks(), antTypeRegistry);
        eggs.add(egg);
    }

    @Override
    public void onMinute() {
        for (Egg egg : eggs) {
            egg.tick();
            if (!egg.isHatched()) {
                continue;
            }

            Optional<AntType> type = egg.getType();
            Ant ant = antFactory.createAnt(home, type.orElseThrow());
            entityManager.addEntity(ant);
        }
        eggs.removeIf(Egg::isHatched);
    }

    public boolean purchaseEgg(AntType type) {
        if (type == null) {
            return false;
        }

        if (!home.spendResources(ResourceType.FOOD, type.foodCost())) {
            // We failed to spend the resources, so we don't add the egg
            return false;
        }

        addEgg(type);
        return true;
    }

    public List<Egg> getEggs() {
        return Collections.unmodifiableList(eggs);
    }
}
