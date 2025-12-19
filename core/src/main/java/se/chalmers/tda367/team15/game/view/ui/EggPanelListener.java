package se.chalmers.tda367.team15.game.view.ui;

/**
 * Listener interface for egg panel UI events.
 * Follows the same pattern as SpeedControlsListener for UI-to-controller
 * communication.
 */
public interface EggPanelListener {
    /**
     * Called when the user attempts to purchase an egg of the specified type.
     *
     * @param typeId the ID of the egg type to purchase
     */
    void onPurchaseEgg(String typeId);
}
