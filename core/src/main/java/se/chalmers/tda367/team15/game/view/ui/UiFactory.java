package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import se.chalmers.tda367.team15.game.view.TextureRegistry;

public class UiFactory {
    private final TextureRegistry textures;
    private final BitmapFont defaultFont;

    public UiFactory(TextureRegistry textures) {
        this.textures = textures;
        this.defaultFont = new BitmapFont();
    }

    public Label.LabelStyle createLabelStyle(float scale, Color color) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(scale);
        return new Label.LabelStyle(font, color);
    }

    public ImageButton createImageButton(String textureName, Runnable onClick) {
        TextureRegion region = textures.get(textureName);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        ImageButton button = new ImageButton(drawable);

        if (onClick != null) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onClick.run();
                }
            });
        }
        return button;
    }

    public ImageButton createToggleButton(String textureName) {
        TextureRegion region = textures.get(textureName);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;
        style.imageDown = drawable;
        style.imageChecked = drawable;

        return new ImageButton(style);
    }

    public Image createImage(String textureName) {
        return new Image(textures.get(textureName));
    }

    public TextureRegionDrawable createDrawable(String textureName) {
        return new TextureRegionDrawable(textures.get(textureName));
    }

    public TextureRegistry getTextures() {
        return textures;
    }

    public void dispose() {
        defaultFont.dispose();
    }
}
