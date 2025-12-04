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
import se.chalmers.tda367.team15.game.model.pheromones.PheromoneType;

public class BottomBarView {
    private static final float BAR_WIDTH = 600f;
    private static final float BAR_HEIGHT = 120f;

    private final Stage stage;
    private final Viewport viewport;

    private final Table root;      // fills screen
    private final Table barTable;  // the visible bottom bar, fixed size
    private Table expandButtonTable;

    private boolean minimized = false;

    // dummy textures
    private final Texture minimizeTex;
    private final Texture expandTex;
    private final Texture barBgTex;

    // Pheromone selection
    private PheromoneSelectionListener pheromoneListener;
    private ButtonGroup<ImageButton> pheromoneButtonGroup;

    public BottomBarView(SpriteBatch batch) {
        viewport = new ScreenViewport();           // use screen pixels
        stage = new Stage(viewport, batch);

        minimizeTex = new Texture(Gdx.files.internal("BottomBar/minimize.png"));
        expandTex   = new Texture(Gdx.files.internal("BottomBar/expand.png"));
        barBgTex    = new Texture(Gdx.files.internal("BottomBar/bottombar_bg.png"));

        // root fills screen, but inner bar has fixed width
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        barTable = new Table();
        barTable.setBackground(new TextureRegionDrawable(new TextureRegion(barBgTex)));

        // Add the bar to the root at bottom-center with fixed size
        root.bottom();
        root.add(barTable).center().width(BAR_WIDTH).height(BAR_HEIGHT);

        buildBarContents();

        // create floating expand button (hidden initially)
        expandButtonTable = createExpandButton();
        expandButtonTable.setVisible(false);
        stage.addActor(expandButtonTable); // floating at stage level

    }

    /**
     * Sets the listener for pheromone selection events.
     * @param listener The listener to notify when a pheromone type is selected.
     */
    public void setPheromoneSelectionListener(PheromoneSelectionListener listener) {
        this.pheromoneListener = listener;
    }

    private void buildBarContents() {
        // Pheromone buttons group - these select pheromone types
        HorizontalGroup pheromoneGroup = createPheromoneButtonGroup();

        // Other buttons group
        HorizontalGroup otherGroup = createButtonGroup(
            new String[] { "BottomBar/btn5.png", "BottomBar/btn6.png" },
            new String[] { "Button 5", "Button 6" }
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
        barTable.add(pheromoneGroup).left().padLeft(12);
        barTable.add(otherGroup).left().padLeft(40);
        barTable.add().expandX();
        barTable.add(minimizeBtn).right().padRight(10).size(40, 40);
    }

    private HorizontalGroup createPheromoneButtonGroup() {
        HorizontalGroup group = new HorizontalGroup();
        group.space(10);

        BitmapFont font = new BitmapFont();

        // Pheromone types with their textures and labels
        String[] textureNames = {
            "BottomBar/btn1.png",  // Gather
            "BottomBar/btn2.png",  // Attack
            "BottomBar/btn3.png",  // Explore
            "BottomBar/btn4.png"   // Delete/Erase
        };
        String[] labels = { "Gather", "Attack", "Explore", "Erase" };
        // null at end represents delete mode
        PheromoneType[] types = { PheromoneType.GATHER, PheromoneType.ATTACK, PheromoneType.EXPLORE, null };

        pheromoneButtonGroup = new ButtonGroup<>();
        pheromoneButtonGroup.setMinCheckCount(1);
        pheromoneButtonGroup.setMaxCheckCount(1);
        pheromoneButtonGroup.setUncheckLast(true);

        for (int i = 0; i < textureNames.length; i++) {
            final PheromoneType type = types[i];

            Texture tex = new Texture(Gdx.files.internal(textureNames[i]));
            TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));

            // Create a style with checked state highlighting
            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = drawable;
            style.imageDown = drawable;
            style.imageChecked = drawable; // same texture, but button will be "checked"

            ImageButton btn = new ImageButton(style);

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (pheromoneListener != null) {
                        pheromoneListener.onPheromoneSelected(type);
                    }
                }
            });

            pheromoneButtonGroup.add(btn);

            // label below the button
            Label desc = new Label(labels[i], new Label.LabelStyle(font, Color.WHITE));
            desc.setAlignment(Align.center);

            VerticalGroup v = new VerticalGroup();
            v.center();
            v.addActor(btn);
            v.addActor(desc);

            group.addActor(v);
        }

        // Default to first button (Gather) checked
        pheromoneButtonGroup.getButtons().first().setChecked(true);

        return group;
    }

    private HorizontalGroup createButtonGroup(String[] textureNames, String[] descriptions) {
        HorizontalGroup group = new HorizontalGroup();
        group.space(10);

        for (int i = 0; i < textureNames.length; i++) {
            Texture tex = new Texture(Gdx.files.internal(textureNames[i]));
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
    private void updateExpandPosition() {
        float worldWidth = stage.getViewport().getWorldWidth();
        float worldHeight = stage.getViewport().getWorldHeight();

        float centerX = worldWidth / 2f;

        float rightEdge = centerX + (BAR_WIDTH / 2f);

        float x = rightEdge - 50f;
        float y = 10f;
        expandButtonTable.setPosition(x, y, Align.bottomLeft);
    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();
    }

    public void resize(int w, int h) {
        viewport.update(w, h, true);
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
