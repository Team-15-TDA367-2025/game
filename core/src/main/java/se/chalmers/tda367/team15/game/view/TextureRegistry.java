package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class TextureRegistry {
    private final ObjectMap<String, TextureRegion> textures = new ObjectMap<>();

    public TextureRegistry() {
        loadAll();
        createPixelTexture();
    }

    /** Scans the assets directory and loads all `.png` files */
    private void loadAll() {
        FileHandle root = Gdx.files.internal("./"); // ./assets
        for (FileHandle file : root.list()) {
            if (!file.extension().equalsIgnoreCase("png")) {
                continue;
            }

            String name = file.nameWithoutExtension();
            Texture texture = new Texture(file);
            textures.put(name, new TextureRegion(texture));
        }
    }

    public TextureRegion get(String name) {
        TextureRegion region = textures.get(name);
        if (region == null) {
            throw new IllegalArgumentException("Texture not found: " + name);
        }
        return region;
    }

    public void dispose() {
        for (TextureRegion region : textures.values()) {
            region.getTexture().dispose();
        }
        textures.clear();
    }
    // AI generated
    private void createPixelTexture() {
    if (!textures.containsKey("pixel")) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f); // white
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        textures.put("pixel", new TextureRegion(texture));
        pixmap.dispose();
    }
}
}
