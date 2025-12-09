package se.chalmers.tda367.team15.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import se.chalmers.tda367.team15.game.model.GameEndReason;
import se.chalmers.tda367.team15.game.model.GameStats;
import se.chalmers.tda367.team15.game.screens.game.GameFactory;

public class EndScreen extends ScreenAdapter {
    private final Game game;
    private BitmapFont font;
    private Stage stage;
    GameEndReason reason;
    String endMessage;
    public EndScreen(Game game, GameEndReason reason) {
        this.game = game;
        this.reason = reason;
    }

    @Override
    public void show() {

        switch (reason) {
            case ALL_ANTS_DEAD:
                endMessage = "All your ants are dead!";
                break;
            case STARVATION:
                endMessage = "Your colony has starved!";
                break;
            default:
                endMessage = "Unknown reason";
                break;
        }

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = new BitmapFont();
        font.getData().setScale(3); // Make text bigger

        TextButtonStyle buttonStyle = simpleButtonStyle(font);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label messageLabel = new Label(endMessage, labelStyle);
        Label highScoreLabel = new Label("Highscore: " + GameStats.loadHighScore(), labelStyle);
        TextButton playAgainButton = new TextButton("Play Again", buttonStyle);

        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(GameFactory.createGameScreen(game));
                dispose();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(messageLabel).padBottom(40).row();
        table.add(highScoreLabel).padBottom(40).row();
        table.add(playAgainButton).width(400).height(100);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }

    private TextButtonStyle simpleButtonStyle(BitmapFont font) {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        style.up = new TextureRegionDrawable(texture);

        Pixmap pixmapDown = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmapDown.setColor(Color.LIGHT_GRAY);
        pixmapDown.fill();
        Texture textureDown = new Texture(pixmapDown);
        pixmapDown.dispose();
        style.down = new TextureRegionDrawable(textureDown);

        return style;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}