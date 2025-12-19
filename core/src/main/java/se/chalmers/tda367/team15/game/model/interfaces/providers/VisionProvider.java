package se.chalmers.tda367.team15.game.model.interfaces.providers;

import se.chalmers.tda367.team15.game.model.interfaces.HasPosition;

public interface VisionProvider extends HasPosition {
    int getVisionRadius();
}
