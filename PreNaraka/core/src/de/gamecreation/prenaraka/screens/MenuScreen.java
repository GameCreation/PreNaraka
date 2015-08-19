package de.gamecreation.prenaraka.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import de.gamecreation.prenaraka.game.Assets;
import de.gamecreation.prenaraka.screens.transitions.ScreenTransitionFade;
import de.gamecreation.prenaraka.util.AudioManager;
import de.gamecreation.prenaraka.util.CharacterSkin;
import de.gamecreation.prenaraka.util.Constants;
import de.gamecreation.prenaraka.util.GamePreferences;

public class MenuScreen extends AbstractGameScreen {
	//TODO Fix Bug: can't click Buttons till you resize the Window 
	
	private static final String TAG = MenuScreen.class.getName();

	private Skin skinLibgdx;

	private Stage stage;

	// menu
	private TextButton btnMenuPlay;
	private TextButton btnMenuOptions;
	// options
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private CheckBox chkMusic;
	private Slider sldMusic;
	private SelectBox selCharSkin;
	private Image imgCharSkin;
	private CheckBox chkShowFpsCounter;
	// debug
	private final float DEBUG_REBUILD_INTERVAL = -5.0f;
	private boolean debugEnabled = true;
	private float debugRebuildStage;
	// Schader
	private CheckBox chkUseMonochromeShader;

	public MenuScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		if (debugEnabled) {
			debugRebuildStage -= deltaTime;
			stage.setDebugAll(true);
			if (debugRebuildStage <= 0 && DEBUG_REBUILD_INTERVAL >= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}
		stage.act(deltaTime);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update((int) Constants.VIEWPORT_GUI_WIDTH,
				(int) Constants.VIEWPORT_GUI_HEIGHT, false);

	}

	@Override
	public void show() {
		stage = new Stage();
		rebuildStage();

	}

	@Override
	public void hide() {
		stage.dispose();
		skinLibgdx.dispose();
	}

	@Override
	public void pause() {
		Gdx.app.debug(TAG, "PAUSE");
	}

	private void rebuildStage() {
		skinLibgdx = (Skin) Assets.instance
				.getObjectFromAssetManager("skin_ui");

		// build all layers
		Array<Table> tableArray = new Array<Table>();
		tableArray.add(buildControlsLayer());
		Table layerOptionsWindow = buildOptionsWindowLayer();
		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		for (Table table : tableArray) {
			stack.add(table);
		}
		stage.addActor(layerOptionsWindow);
	}

	private Table buildControlsLayer() {
		Table layer = new Table();
		layer.bottom();//layer.right().bottom();
		// + Play Button
		btnMenuPlay = new TextButton("Play", skinLibgdx);
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});
		layer.row();
		// + Options Button
		btnMenuOptions = new TextButton("Options", skinLibgdx);
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});
		if (debugEnabled)
			layer.debug();
		return layer;
	}

	private void onPlayClicked() {
		ScreenTransition transition = ScreenTransitionFade.init(0.75f);
		game.setScreen(new GameScreen(game), transition);
	}

	private void onOptionsClicked() {
		loadSettings();
		showMenuButtons(false);
		showOptionsWindow(true, true);
	}

	private Table buildOptionsWindowLayer() {
		winOptions = new Window("Options", skinLibgdx);
		// + Audio Settings: Sound/Music CheckBox and Volume Slider
		winOptions.add(buildOptWinAudioSettings()).row();
		// + Character Skin: Selection Box (White, Gray, Brown)
		 winOptions.add(buildOptWinSkinSelection()).row();
		// + Debug: Show FPS Counter
		winOptions.add(buildOptWinDebug()).row();
		// + Separator and Buttons (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
		// Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);
		// Hide options window by default
		showOptionsWindow(false, false);
		if (debugEnabled)
			winOptions.debug();
		// Let TableLayout recalculate widget sizes and positions
		winOptions.pack();
		// Move options window to bottom right corner
		winOptions.setPosition(
				Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winOptions;
	}

	private void loadSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		selCharSkin.setSelected(prefs.charSkin);
		onCharSkinSelected(prefs.charSkin);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
		// Shader
		chkUseMonochromeShader.setChecked(prefs.useMonochromeShader);
	}

	private void saveSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.charSkin = selCharSkin.getSelectedIndex(); 
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		// Shader
		prefs.useMonochromeShader = chkUseMonochromeShader.isChecked();
		prefs.save();
	}

	private void onCharSkinSelected(int index) {
//		 CharacterSkin skin = CharacterSkin.values()[index];
//		 imgCharSkin.setColor(skin.getColor());
	}

	private void onSaveClicked() {
		saveSettings();
		onCancelClicked();
	}

	private void onCancelClicked() {
		showMenuButtons(true);
		showOptionsWindow(false, true);
		AudioManager.instance.onSettingsUpdated();
	}

	private Table buildOptWinAudioSettings() {
		Table tbl = new Table();
		// + Title: "Audio"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE))
				.colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Sound" label, sound volume slider
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);
		tbl.add(new Label("Sound", skinLibgdx));
		sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldSound);
		tbl.row();
		// + Checkbox, "Music" label, music volume slider
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(chkMusic);
		tbl.add(new Label("Music", skinLibgdx));
		sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldMusic);
		tbl.row();
		return tbl;
	}

	private Table buildOptWinSkinSelection() {
		Table tbl = new Table();
		// + Title: "Character Skin"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Character Skin", skinLibgdx, "default-font",
				Color.ORANGE)).colspan(2);
		tbl.row();
		// + Drop down box filled with skin items
		selCharSkin = new SelectBox(skinLibgdx);
		 selCharSkin.setItems(CharacterSkin.values());
		selCharSkin.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCharSkinSelected(((SelectBox) actor).getSelectedIndex());
			}
		});
		tbl.add(selCharSkin).width(120).padRight(20);
		// + Skin preview image
//		 imgCharSkin = new Image(Assets.instance.getObjectFromAssetManager("Hero");
		tbl.add(imgCharSkin).width(50).height(50);
		return tbl;
	}

	private Table buildOptWinDebug() {
		Table tbl = new Table();
		// + Title: "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED))
				.colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Show FPS Counter" label
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Show FPS Counter", skinLibgdx));
		tbl.add(chkShowFpsCounter);
		tbl.row();
		// + Checkbox, "Use Monochrome Shader" label
		chkUseMonochromeShader = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Use Monochrome Shader", skinLibgdx));
		tbl.add(chkUseMonochromeShader);
		tbl.row();
		return tbl;
	}

	private Table buildOptWinButtons() {
		Table tbl = new Table();
		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();
		// + Save Button with event handler
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});
		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
		tbl.add(btnWinOptCancel);
		btnWinOptCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});
		return tbl;
	}

	// Button Animation
	private void showMenuButtons(boolean visible) {
		float moveDuration = 1.0f;
		Interpolation moveEasing = Interpolation.swing;
		float delayOptionsButton = 0.25f;
		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0 * (visible ? -1 : 1);
		final Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
		btnMenuOptions.addAction(sequence(delay(delayOptionsButton),
				moveBy(moveX, moveY, moveDuration, moveEasing)));
		SequenceAction seq = sequence();
		if (visible)
			seq.addAction(delay(delayOptionsButton + moveDuration));
		btnMenuPlay.setTouchable(touchEnabled); // edited by ME
		btnMenuOptions.setTouchable(touchEnabled); // edited by ME
		stage.addAction(seq);
	}

	// Options Window Animation
	private void showOptionsWindow(boolean visible, boolean animated) {
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		winOptions.setTouchable(touchEnabled); // edited by ME
		winOptions.addAction(sequence(alpha(alphaTo, duration)));
	}
}
