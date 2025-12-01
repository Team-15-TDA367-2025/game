package se.chalmers.tda367.team15.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BottomBarView {
    private static final float BAR_WIDTH = 600f;
    private static final float BAR_HEIGHT = 120f;

    private final Stage stage;
    private final Viewport viewport;

    // UI pieces
    private final Table root;      // fills screen
    private final Table barTable;  // the visible bottom bar, fixed size
    private Table expandButtonTable;

    private boolean minimized = false;

    // dummy textures
    private final Texture minimizeTex;
    private final Texture expandTex;
    private final Texture barBgTex;

    public BottomBarView(SpriteBatch batch) {
        viewport = new ScreenViewport();           // use screen pixels
        stage = new Stage(viewport, batch);

        // load dummy textures (replace with real assets later)
        minimizeTex = new Texture(Gdx.files.internal("BottomBar/minimize.png"));
        expandTex   = new Texture(Gdx.files.internal("BottomBar/expand.png"));
        barBgTex    = new Texture(Gdx.files.internal("BottomBar/bottombar_bg.png"));

        // root fills screen, but inner bar has fixed width
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        barTable = new Table();
        barTable.setBackground(new TextureRegionDrawable(new TextureRegion(barBgTex)));
        // IMPORTANT: do NOT call setFillParent(true) on barTable

        // Add the bar to the root at bottom-center with fixed size
        root.bottom();
        root.add(barTable).center().width(BAR_WIDTH).height(BAR_HEIGHT);

        buildBarContents();

        // create floating expand button (hidden initially)
        expandButtonTable = createExpandButton();
        expandButtonTable.setVisible(false);
        stage.addActor(expandButtonTable); // floating at stage level
        // position will be updated on resize
    }

    private void buildBarContents() {
        // Two groups of 3 buttons each
        HorizontalGroup group1 = new HorizontalGroup();
        HorizontalGroup group2 = new HorizontalGroup();

        BitmapFont font = new BitmapFont();
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

        group1 = createButtonGroup(
            new String[]{"BottomBar/btn1.png", "BottomBar/btn2.png", "BottomBar/btn3.png"},
            new String[]{"Button 1", "Button 2", "Button 3"}
        );

        group2 = createButtonGroup(
            new String[] { "BottomBar/btn4.png", "BottomBar/btn5.png", "BottomBar/btn6.png" },
            new String[] { "Button 4", "Button 5", "Button 6" }
        );

        // Minimize button on right
        ImageButton minimizeBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(minimizeTex)));
        minimizeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                setMinimized(true);
            }
        });

        // layout inside barTable
        barTable.left();
        barTable.add(group1).left().padLeft(12);
        barTable.add(group2).left().padLeft(40);
        barTable.add().expandX(); // take middle empty space
        barTable.add(minimizeBtn).right().padRight(10).size(40, 40);
    }

    private HorizontalGroup createButtonGroup(String[] textureNames, String[] descriptions) {
        HorizontalGroup group = new HorizontalGroup();
        group.space(10);

        for (int i = 0; i < textureNames.length; i++) {
            Texture tex = new Texture(textureNames[i]);  // each button has unique texture
            TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));

            ImageButton btn = new ImageButton(drawable);

            // label below the button
            BitmapFont font = new BitmapFont();
            Label desc = new Label(descriptions[i], new Label.LabelStyle(font, Color.WHITE));
            desc.setAlignment(Align.center);

            VerticalGroup v = new VerticalGroup();
            v.center();
            v.addActor(btn);
            v.addActor(desc);

            group.addActor(v);
        }
        return group;
    }

    private Table createExpandButton() {
        BitmapFont font = new BitmapFont();
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

        ImageButton expandBtn = new ImageButton(new TextureRegionDrawable(new TextureRegion(expandTex)));
        expandBtn.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                setMinimized(false);
            }
        });

        Label lbl = new Label("expand", ls);

        Table t = new Table();
        t.add(expandBtn).size(40, 40);
        t.row();
        t.add(lbl).padTop(4f);
        // initial position 0, will be updated in resize() to align with minimize spot
        t.setVisible(false);
        return t;
    }

    private void setMinimized(boolean m) {
        minimized = m;
        barTable.setVisible(!m);
        expandButtonTable.setVisible(m);
        // Make sure expand button is on top visually
        if (m) expandButtonTable.toFront();
    }

    // update expand button position so it appears where the minimize button was
    // call this from resize() and after stage viewport changes
    private void updateExpandPosition() {
        // compute screen center x
        float worldWidth = stage.getViewport().getWorldWidth();
        float worldHeight = stage.getViewport().getWorldHeight();

        float centerX = worldWidth / 2f;
        // right edge of barTable (in stage coords)
        float rightEdge = centerX + (BAR_WIDTH / 2f);

        // place expand button slightly above bottom, aligned where minimize was:
        float x = rightEdge - 50f; // tune offset so it matches minimize location
        float y = 10f; // some padding above bottom
        expandButtonTable.setPosition(x, y, Align.bottomLeft);
    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

    public void resize(int w, int h) {
        viewport.update(w, h, true);
        // reposition floating expand button after viewport update
        updateExpandPosition();
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        minimizeTex.dispose();
        expandTex.dispose();
        barBgTex.dispose();
        stage.dispose();
    }
}
