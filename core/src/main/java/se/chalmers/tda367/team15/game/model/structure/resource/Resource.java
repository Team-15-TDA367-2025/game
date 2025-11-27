package se.chalmers.tda367.team15.game.model.structure.resource;

import com.badlogic.gdx.math.GridPoint2;

import se.chalmers.tda367.team15.game.model.structure.Structure;

public class Resource extends Structure {
    private ResourceType type;
    private int quantity;

    public Resource(GridPoint2 position, String textureName, int radius, ResourceType type, int quantity) {
        super(position, textureName, radius);
        this.type = type;
        this.quantity = quantity;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
