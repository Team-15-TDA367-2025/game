package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import se.chalmers.tda367.team15.game.view.TextureRegistry;

public class UiFactory {
    private final TextureRegistry textures;
    private final BitmapFont defaultFont;
    private NinePatchDrawable buttonBackground;
    private NinePatchDrawable buttonBackgroundHover;
    private NinePatchDrawable buttonBackgroundPressed;
    private NinePatchDrawable buttonBackgroundChecked;
    private NinePatchDrawable panelBackground;
    private NinePatchDrawable areaBackground;

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

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;
        style.up = getButtonBackground();
        style.over = getButtonBackgroundHover();
        style.down = getButtonBackgroundPressed();

        ImageButton button = new ImageButton(style);

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

    public NinePatchDrawable getPanelBackground() {
        if (panelBackground == null) {
            NinePatch patch = new NinePatch(textures.get("panel"), 4, 4, 4, 4);
            patch.scale(UiTheme.NINE_PATCH_SCALE, UiTheme.NINE_PATCH_SCALE);
            panelBackground = new NinePatchDrawable(patch);
        }
        return panelBackground;
    }

    public NinePatchDrawable getButtonBackground() {
        if (buttonBackground == null) {
            NinePatch patch = new NinePatch(textures.get("button"), 3, 3, 3, 6);
            patch.scale(UiTheme.NINE_PATCH_SCALE, UiTheme.NINE_PATCH_SCALE);
            buttonBackground = new NinePatchDrawable(patch);
        }
        return buttonBackground;
    }

    public NinePatchDrawable getButtonBackgroundHover() {
        if (buttonBackgroundHover == null) {
            NinePatch patch = new NinePatch(textures.get("button"), 3, 3, 3, 6);
            patch.scale(UiTheme.NINE_PATCH_SCALE, UiTheme.NINE_PATCH_SCALE);
            NinePatchDrawable drawable = new NinePatchDrawable(patch);
            drawable.getPatch().setColor(UiTheme.BUTTON_HOVER_TINT);
            buttonBackgroundHover = drawable;
        }
        return buttonBackgroundHover;
    }

    public NinePatchDrawable getButtonBackgroundPressed() {
        if (buttonBackgroundPressed == null) {
            NinePatch patch = new NinePatch(textures.get("button"), 3, 3, 3, 6);
            patch.scale(UiTheme.NINE_PATCH_SCALE, UiTheme.NINE_PATCH_SCALE);
            NinePatchDrawable drawable = new NinePatchDrawable(patch);
            drawable.getPatch().setColor(UiTheme.BUTTON_PRESSED_TINT);
            buttonBackgroundPressed = drawable;
        }
        return buttonBackgroundPressed;
    }

    public NinePatchDrawable getButtonBackgroundChecked() {
        if (buttonBackgroundChecked == null) {
            NinePatch patch = new NinePatch(textures.get("button"), 3, 3, 3, 6);
            patch.scale(UiTheme.NINE_PATCH_SCALE, UiTheme.NINE_PATCH_SCALE);
            NinePatchDrawable drawable = new NinePatchDrawable(patch);
            drawable.getPatch().setColor(UiTheme.BUTTON_CHECKED_TINT);
            buttonBackgroundChecked = drawable;
        }
        return buttonBackgroundChecked;
    }

    public NinePatchDrawable getAreaBackground() {
        if (areaBackground == null) {
            NinePatch patch = new NinePatch(textures.get("area"), 2, 2, 3, 3);
            patch.scale(UiTheme.NINE_PATCH_SCALE, UiTheme.NINE_PATCH_SCALE);
            areaBackground = new NinePatchDrawable(patch);
        }
        return areaBackground;
    }

    public ImageButton createToggleButton(String textureName) {
        TextureRegion region = textures.get(textureName);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;
        style.up = getButtonBackground();
        style.over = getButtonBackgroundHover();
        style.down = getButtonBackgroundPressed();
        style.checked = getButtonBackgroundChecked();

        return new ImageButton(style);
    }

    public TextButton createTextButton(String text, Runnable onClick) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(UiTheme.FONT_SCALE_DEFAULT);

        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = getButtonBackground();
        style.over = getButtonBackgroundHover();
        style.down = getButtonBackgroundPressed();
        style.checked = getButtonBackground();
        style.fontColor = Color.WHITE;

        TextButton button = new TextButton(text, style);

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

    public TextButton createToggleTextButton(String text) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(UiTheme.FONT_SCALE_DEFAULT);

        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = getButtonBackground();
        style.over = getButtonBackgroundHover();
        style.down = getButtonBackgroundPressed();
        style.checked = getButtonBackgroundChecked();
        style.fontColor = Color.WHITE;
        style.checkedFontColor = Color.WHITE;

        return new TextButton(text, style);
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
