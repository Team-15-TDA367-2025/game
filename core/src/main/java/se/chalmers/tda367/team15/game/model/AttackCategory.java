package se.chalmers.tda367.team15.game.model;

import se.chalmers.tda367.team15.game.model.interfaces.CanBeAttacked;

/**
 * Each {@link CanBeAttacked} must define what category it's in. Each Attack
 * capable thing then uses these categories
 * to determine their own target priorities.
 */
public enum AttackCategory {
    WORKER_ANT,
    TERMITE
}
