package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TutorialView {

    private static final String TUTORIAL_TEXT = "Welcome to your colony!\n\n" +
            "To control your ants, press and drag from the colony base to draw a pheromone trail. " +
            "Ants will immediately follow the trail you create: workers follow gather, scouts follow explore, " +
            "and soldiers follow attack. You can remove any pheromone trail at any time using the erase button. " +
            "Buy new ants in the bottom right to be able to defend yourself from termites that come at night!";

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_PADDING = 20;
    private static final int CONTENT_PADDING = 10;
    private static final int BUTTON_BOTTOM_PADDING = 10;

    private static final Color WINDOW_BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BUTTON_UP_COLOR = Color.WHITE;
    private static final Color BUTTON_DOWN_COLOR = Color.LIGHT_GRAY;

    private final Table popupTable;
    private final Skin skin;
    private boolean visible;

    public TutorialView(Stage popupStage) {
        this.skin = createSkin();
        this.popupTable = createPopupTable();
        this.visible = true;

        popupStage.addActor(popupTable);
    }

    private Skin createSkin() {
        Skin newSkin = new Skin();

        BitmapFont font = new BitmapFont();
        newSkin.add("default", font);

        Texture whiteTexture = createWhiteTexture();
        newSkin.add("white", whiteTexture);

        newSkin.add("default", createLabelStyle(font));
        newSkin.add("default", createButtonStyle(font, newSkin));

        return newSkin;
    }

    private Texture createWhiteTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Label.LabelStyle createLabelStyle(BitmapFont font) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = TEXT_COLOR;
        return labelStyle;
    }

    private TextButton.TextButtonStyle createButtonStyle(BitmapFont font, Skin skin) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = TEXT_COLOR;
        buttonStyle.up = skin.newDrawable("white", BUTTON_UP_COLOR);
        buttonStyle.down = skin.newDrawable("white", BUTTON_DOWN_COLOR);
        return buttonStyle;
    }

    private Table createPopupTable() {
        Table table = new Table();
        table.setFillParent(true);

        Table window = createWindowTable();
        table.add(window).center();

        return table;
    }

    private Table createWindowTable() {
        Table window = new Table();
        window.setBackground(skin.newDrawable("white", WINDOW_BACKGROUND_COLOR));
        window.pad(WINDOW_PADDING);

        TextButton closeButton = createCloseButton();
        Label tutorialText = createTutorialLabel();

        window.add(closeButton).expandX().right().top().padBottom(BUTTON_BOTTOM_PADDING);
        window.row();
        window.add(tutorialText).width(WINDOW_WIDTH).pad(CONTENT_PADDING);

        return window;
    }

    private TextButton createCloseButton() {
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        return closeButton;
    }

    private Label createTutorialLabel() {
        Label label = new Label(TUTORIAL_TEXT, skin);
        label.setWrap(true);
        return label;
    }

    public void hide() {
        visible = false;
        popupTable.remove();
    }

    public boolean isVisible() {
        return visible;
    }

    public void dispose() {
        skin.dispose();
    }
}