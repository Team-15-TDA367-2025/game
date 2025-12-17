package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

import se.chalmers.tda367.team15.game.model.interfaces.TimeCycleDataProvider;

public class TopBarView extends Table {
    private final UiSkin uiFactory;

    private final Label dayLabel;
    private final Label timeLabel;
    private final Label resource1Value;
    private final Label resource2Value;
    private SpeedControlsListener speedControlsListener;

    public TopBarView(UiSkin uiFactory) {
        this.uiFactory = uiFactory;

        setBackground(uiFactory.getPanelBackground());
        // Remove padding if requested
        // pad(UiTheme.PADDING_MEDIUM);
        padTop(UiTheme.PADDING_MEDIUM).padBottom(UiTheme.PADDING_MEDIUM);

        Label.LabelStyle labelStyle = uiFactory.createLabelStyle(UiTheme.FONT_SCALE_LARGE, Color.BLACK);

        dayLabel = new Label("Day 1", labelStyle);
        timeLabel = new Label("07:59", labelStyle);

        Table speedTable = createSpeedControls();

        resource1Value = new Label("1337", labelStyle);
        resource2Value = new Label("420", labelStyle);

        Stack resourceBar1 = createResourceBar("ant", resource1Value);
        Stack resourceBar2 = createResourceBar("resource", resource2Value);

        Table resourceStack = new Table();
        resourceStack.add(resourceBar1).width(200f).growY().padBottom(UiTheme.PADDING_SMALL);
        resourceStack.row();
        resourceStack.add(resourceBar2).width(200f).growY();
        resourceStack.row();

        left();
        add(dayLabel).left().padRight(UiTheme.PADDING_MEDIUM)
                .minWidth(UiTheme.DAY_LABEL_MIN_WIDTH);
        add(timeLabel).left().padRight(UiTheme.PADDING_MEDIUM).minWidth(UiTheme.TIME_LABEL_MIN_WIDTH);
        add(speedTable).padRight(UiTheme.PADDING_MEDIUM);
        add(resourceStack).growY().padRight(UiTheme.PADDING_MEDIUM);
    }

    private Table createSpeedControls() {
        TextButton pauseBtn = uiFactory.createToggleTextButton("Pause");
        TextButton playBtn = uiFactory.createToggleTextButton("Play");
        TextButton fastBtn = uiFactory.createToggleTextButton("Fast");

        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                speedControlsListener.onPause();
            }
        });
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                speedControlsListener.onNormalSpeed();
            }
        });
        fastBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                speedControlsListener.onFastSpeed();
            }
        });

        ButtonGroup<TextButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);
        group.setUncheckLast(true);
        group.add(pauseBtn, playBtn, fastBtn);
        playBtn.setChecked(true);

        Table speedTable = new Table();
        speedTable.add(pauseBtn).padRight(UiTheme.PADDING_SMALL);
        speedTable.add(playBtn).padRight(UiTheme.PADDING_SMALL);
        speedTable.add(fastBtn);

        return speedTable;
    }

    private Stack createResourceBar(String iconTextureName, Label valueLabel) {
        // Background table
        Table bg = new Table();
        bg.setBackground(uiFactory.getAreaBackground());
        bg.pad(UiTheme.PADDING_SMALL);
        bg.add().width(20f); // Spacer for the icon
        bg.add(valueLabel).right().padRight(UiTheme.PADDING_SMALL);

        // Icon
        Image icon = uiFactory.createImage(iconTextureName);
        icon.setScaling(Scaling.fit); // Keep aspect ratio

        Stack stack = new Stack();
        stack.add(bg);

        // Container for icon to align it left and centered vertically, slightly
        // overlapping
        Container<Image> iconContainer = new Container<>(icon);
        // Set explicit size or max size, but allow scaling
        iconContainer.size(32f, 48f); // Adjust size to fit 32x48 roughly, or use Scaling
        iconContainer.left().padLeft(-10f); // Pull it out to the left slightly

        stack.add(iconContainer);

        return stack;
    }

    public void update(TimeCycleDataProvider timeProvider, int antCount, int resourceCount, int consumption) {
        dayLabel.setText("Day " + timeProvider.getGameTime().totalDays());
        timeLabel.setText(String.format("%02d:%02d", timeProvider.getGameTime().currentHour(),
                timeProvider.getGameTime().currentMinute()));
        resource1Value.setText(String.valueOf(antCount));
        resource2Value.setText(String.format("%d (%d/d)", resourceCount, consumption));

    }

    public void setSpeedControlsListener(SpeedControlsListener speedControlsListener) {
        this.speedControlsListener = speedControlsListener;
    }
}
