package se.chalmers.tda367.team15.game.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import se.chalmers.tda367.team15.game.model.TimeCycle;

public class TopBarView extends Table {
    private final UiFactory uiFactory;

    private final Label dayLabel;
    private final Label timeLabel;
    private final Label resource1Value;
    private final Label resource2Value;

    public TopBarView(UiFactory uiFactory) {
        this.uiFactory = uiFactory;

        setBackground(uiFactory.createDrawable("TopBar/topbar_bg"));
        
        Label.LabelStyle labelStyle = uiFactory.createLabelStyle(UiTheme.FONT_SCALE_LARGE, Color.BLACK);

        dayLabel = new Label("Day 1", labelStyle);
        timeLabel = new Label("07:59", labelStyle);

        Table speedTable = createSpeedControls();

        resource1Value = new Label("1337", labelStyle);
        resource2Value = new Label("420", labelStyle);

        Table resourceBar1 = createResourceBar("TopBar/ant", resource1Value);
        Table resourceBar2 = createResourceBar("TopBar/berry", resource2Value);

        Table resourceStack = new Table();
        resourceStack.add(resourceBar1).growX().height(UiTheme.RESOURCE_BAR_HEIGHT).padBottom(UiTheme.PADDING_TINY);
        resourceStack.row();
        resourceStack.add(resourceBar2).growX().height(UiTheme.RESOURCE_BAR_HEIGHT);

        Table rightButtons = createRightButtons();

        left();
        add(dayLabel).left().padLeft(UiTheme.PADDING_MEDIUM).padRight(UiTheme.PADDING_XLARGE).minWidth(UiTheme.DAY_LABEL_MIN_WIDTH);
        add(timeLabel).left().padRight(UiTheme.PADDING_XLARGE).minWidth(UiTheme.TIME_LABEL_MIN_WIDTH);
        add(speedTable).padRight(UiTheme.PADDING_XXLARGE);
        add(resourceStack).width(UiTheme.RESOURCE_BAR_WIDTH).padRight(UiTheme.PADDING_XXLARGE);
        add(rightButtons).right().padRight(UiTheme.PADDING_SMALL);
    }

    private Table createSpeedControls() {
        ImageButton pauseBtn = uiFactory.createImageButton("TopBar/pause", null);
        ImageButton playBtn = uiFactory.createImageButton("TopBar/play", null);
        ImageButton fastBtn = uiFactory.createImageButton("TopBar/fast", null);

        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);
        group.setUncheckLast(true);
        group.add(pauseBtn, playBtn, fastBtn);
        playBtn.setChecked(true);

        Table speedTable = new Table();
        speedTable.add(pauseBtn).size(UiTheme.ICON_SIZE_MEDIUM).padRight(UiTheme.PADDING_SMALL);
        speedTable.add(playBtn).size(UiTheme.ICON_SIZE_MEDIUM).padRight(UiTheme.PADDING_SMALL);
        speedTable.add(fastBtn).size(UiTheme.ICON_SIZE_MEDIUM);

        return speedTable;
    }

    private Table createResourceBar(String iconTextureName, Label valueLabel) {
        Table table = new Table();
        table.setBackground(uiFactory.createDrawable("TopBar/resource_bg"));
        table.add(uiFactory.createImage(iconTextureName)).size(UiTheme.ICON_SIZE_SMALL).padLeft(UiTheme.PADDING_SMALL).left();
        table.add(valueLabel).expandX().right().padRight(UiTheme.PADDING_MEDIUM);
        return table;
    }

    private Table createRightButtons() {
        ImageButton statsBtn = uiFactory.createImageButton("TopBar/stats", null);
        ImageButton settingsBtn = uiFactory.createImageButton("TopBar/settings", null);

        Table rightButtons = new Table();
        rightButtons.add(statsBtn).size(UiTheme.ICON_SIZE_LARGE).padRight(UiTheme.PADDING_SMALL);
        rightButtons.add(settingsBtn).size(UiTheme.ICON_SIZE_LARGE);

        return rightButtons;
    }

    public void update(TimeCycle.GameTime gameTime, int antCount, int resourceCount) {
        dayLabel.setText("Day " + gameTime.totalDays());
        timeLabel.setText(String.format("%02d:%02d", gameTime.currentHour(), gameTime.currentMinute()));
        resource1Value.setText(String.valueOf(antCount));
        resource2Value.setText(String.valueOf(resourceCount));
    }
}
