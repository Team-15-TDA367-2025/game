package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;

/**
 * Centralized UI constants for consistent styling across all views.
 */
public final class UiTheme {

    private UiTheme() {
    }

    public static final float UI_SCALE = 1.0f;
    public static final float NINE_PATCH_SCALE = 4.0f;

    public static final float TOP_BAR_HEIGHT = 140f;
    public static final float DAY_LABEL_MIN_WIDTH = 100f;
    public static final float TIME_LABEL_MIN_WIDTH = 80f;

    public static final float BOTTOM_BAR_WIDTH = 640f;
    public static final float BOTTOM_BAR_HEIGHT = 128f;

    public static final float ICON_SIZE_SMALL = 24f;
    public static final float ICON_SIZE_MEDIUM = 32f;
    public static final float ICON_SIZE_LARGE = 64f;

    public static final float PADDING_TINY = 4f;
    public static final float PADDING_SMALL = 8f;
    public static final float PADDING_MEDIUM = 12f;
    public static final float PADDING_LARGE = 16f;
    public static final float PADDING_XLARGE = 24f;
    public static final float PADDING_XXLARGE = 32f;
    public static final float BUTTON_SPACING = 6f;

    public static final float FONT_SCALE_DEFAULT = 1f;
    public static final float FONT_SCALE_LARGE = 2f;

    public static final Color BUTTON_HOVER_TINT = new Color(0.8f, 1f, 0.8f, 1);
    public static final Color BUTTON_PRESSED_TINT = new Color(0.5f, 1f, 0.8f, 1);
    public static final Color BUTTON_CHECKED_TINT = new Color(0.5f, 1f, 0.5f, 1);
}
