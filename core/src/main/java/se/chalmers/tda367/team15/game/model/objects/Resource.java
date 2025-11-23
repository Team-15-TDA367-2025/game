package se.chalmers.tda367.team15.game.model.objects;

public class Resource extends Object {
    private String type;
    private int quantity;

    public Resource(int x, int y, String type, int quantity) {
        super(x, y);
        this.type = type;
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
