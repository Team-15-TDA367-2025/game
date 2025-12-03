package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import se.chalmers.tda367.team15.game.model.TimeCycle;

public class TopBarView {
    private static final float BAR_WIDTH = 770f;
    private static final float BAR_HEIGHT = 95f;

    private final Stage stage;
    private final Viewport viewport;

    private final Table root;
    private final Table barTable;

    // Updatable labels:
    private final Label dayLabel;
    private final Label timeLabel;
    private final Label resource1Value;
    private final Label resource2Value;

    // textures
    private final Texture bgTex;
    private final Texture pauseTex, playTex, fastTex;
    private final Texture antTex, berryTex;
    private final Texture statsTex, settingsTex;
    private final Texture resourceBgTex;

    public TopBarView(SpriteBatch batch) {
        viewport = new ScreenViewport();
        stage = new Stage(viewport, batch);

        // load textures
        bgTex = new Texture(Gdx.files.internal("TopBar/topbar_bg.png"));
        pauseTex = new Texture(Gdx.files.internal("TopBar/pause.png"));
        playTex = new Texture(Gdx.files.internal("TopBar/play.png"));
        fastTex = new Texture(Gdx.files.internal("TopBar/fast.png"));
        antTex = new Texture(Gdx.files.internal("TopBar/ant.png"));
        berryTex = new Texture(Gdx.files.internal("TopBar/berry.png"));
        statsTex = new Texture(Gdx.files.internal("TopBar/stats.png"));
        settingsTex = new Texture(Gdx.files.internal("TopBar/settings.png"));
        resourceBgTex = new Texture(Gdx.files.internal("TopBar/resource_bg.png"));

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        barTable = new Table();
        barTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTex)));

        root.top();
        root.add(barTable).center().width(BAR_WIDTH).height(BAR_HEIGHT);


        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.BLACK);

        dayLabel = new Label("Day 1", ls);
        timeLabel = new Label("07:59", ls);

        // radio buttons
        ImageButton pauseBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(pauseTex)));
        ImageButton playBtn  = new ImageButton(new TextureRegionDrawable(new TextureRegion(playTex)));
        ImageButton fastBtn  = new ImageButton(new TextureRegionDrawable(new TextureRegion(fastTex)));

        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);
        group.setUncheckLast(true);
        group.add(pauseBtn, playBtn, fastBtn);
        playBtn.setChecked(true);

        Table speedTable = new Table();
        speedTable.add(pauseBtn).size(40).padRight(6);
        speedTable.add(playBtn).size(40).padRight(6);
        speedTable.add(fastBtn).size(40);

        resource1Value = new Label("1337", ls);
        resource2Value = new Label("420",  ls);

        Table resourceBar1 = makeResourceBar(antTex, resource1Value);
        Table resourceBar2 = makeResourceBar(berryTex, resource2Value);

        Table resourceStack = new Table();
        resourceStack.add(resourceBar1).growX().height(40).padBottom(4);
        resourceStack.row();
        resourceStack.add(resourceBar2).growX().height(40);

        // right-side buttons
        ImageButton statsBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(statsTex)));
        ImageButton settingsBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTex)));
        Table rightButtons = new Table();
        rightButtons.add(statsBtn).size(46).padRight(8);
        rightButtons.add(settingsBtn).size(46);

        // assemble the row
        barTable.left();
        barTable.add(dayLabel).left().padLeft(10).padRight(18).minWidth(115f);;
        barTable.add(timeLabel).left().padRight(18).minWidth(95f);;
        barTable.add(speedTable).padRight(22);
        barTable.add(resourceStack).width(220).padRight(22);
        barTable.add(rightButtons).right().padRight(8);
    }

    private Table makeResourceBar(Texture iconTex, Label valueLabel) {
        Table t = new Table();
        t.setBackground(new TextureRegionDrawable(new TextureRegion(resourceBgTex)));

        Image icon = new Image(new TextureRegion(iconTex));
        t.add(icon).size(28).padLeft(6).left();
        t.add(valueLabel).expandX().right().padRight(10);
        return t;
    }

    public void update(TimeCycle.GameTime gameTime, int antCount, int resourceCount) {
        dayLabel.setText("Day " + gameTime.totalDays());
        timeLabel.setText(String.format("%02d:%02d", gameTime.currentHour(), gameTime.currentMinute()));
        resource1Value.setText(antCount + "");
        resource2Value.setText(resourceCount + "");
    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    public Stage getStage() { return stage; }

    public void dispose() {
        bgTex.dispose();
        pauseTex.dispose();
        playTex.dispose();
        fastTex.dispose();
        antTex.dispose();
        berryTex.dispose();
        statsTex.dispose();
        settingsTex.dispose();
        resourceBgTex.dispose();
        stage.dispose();
    }
}
