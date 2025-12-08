package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Centralized texture management.
 * Loads textures from assets directory and caches them.
 */
public class TextureRegistry {
    private final ObjectMap<String, TextureRegion> textures = new ObjectMap<>();

    public TextureRegistry() {
        loadDirectory(".");
        loadDirectory("TopBar");
        loadDirectory("BottomBar");
        createPixelTexture();
    }

    /**
     * Loads all PNG files from the specified directory.
     */
    public void loadDirectory(String path) {
        FileHandle dir = Gdx.files.internal(path);
        if (!dir.exists())
            return;

        String prefix = (path.equals(".") || path.equals("./")) ? "" : path + "/";

        for (FileHandle file : dir.list()) {
            if (file.extension().equalsIgnoreCase("png") && !file.name().startsWith("c__")) {
                try {
                    String key = prefix + file.nameWithoutExtension();
                    textures.put(key, new TextureRegion(new Texture(file)));
                } catch (Exception e) {
                    // Skip files that can't be loaded as textures
                    System.err.println("Warning: Could not load texture: " + file.name());
                }
            }
        }
    }

    public TextureRegion get(String name) {
        TextureRegion region = textures.get(name);
        if (region == null) {
            throw new IllegalArgumentException("Texture not found: " + name);
        }
        return region;
    }

    public boolean has(String name) {
        return textures.containsKey(name);
    }

    public void dispose() {
        for (TextureRegion region : textures.values()) {
            region.getTexture().dispose();
        }
        textures.clear();
    }

    private void createPixelTexture() {
        if (!textures.containsKey("pixel")) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(1f, 1f, 1f, 1f);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            textures.put("pixel", new TextureRegion(texture));
            pixmap.dispose();
        }
    }
}
