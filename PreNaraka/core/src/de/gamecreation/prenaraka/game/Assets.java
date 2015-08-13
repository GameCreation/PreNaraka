package de.gamecreation.prenaraka.game;

import java.io.BufferedReader;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import de.gamecreation.prenaraka.util.Constants;

/**
 * load all Assets, load Assets listed in a File: assetsMap.json
 * 
 * @author timomorawitz
 *
 */
public class Assets implements Disposable, AssetErrorListener {
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();

	private SaveManager saveManager;
	private AssetManager assetManager;

	public Object getObjectFromAssetManager(String key) {
		String fileName = saveManager.loadDataValue(key, String.class);
		return assetManager.get(fileName);
	}

	public AssetFonts fonts;

	/**
	 * singleton: prevent instantiation from other classes
	 */
	private Assets() {
	}

	public class AssetFonts {
		// TODO over think this!
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			// set font sizes
			/*
			 * defaultSmall.setScale(0.75f); defaultNormal.setScale(1.0f);
			 * defaultBig.setScale(2.0f);
			 */
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	/**
	 * List of all FileExtensions handled in the AssetManager
	 * 
	 * @author timomorawitz
	 *
	 */
	private enum Extensions {
		Font("fnt", BitmapFont.class), Sound("wav", Sound.class), Music("mp3",
				Music.class), TextureAtlas("pack", TextureAtlas.class), Skin(
				"json", Skin.class);
		// TODO add others from Constants

		public String extension;
		public boolean handled;
		public Class<?> type;

		Extensions(String extension, boolean handled) {
			this.extension = extension;
			this.handled = handled;
		}

		Extensions(String extension, Class<?> type) {
			this(extension, true);
			this.type = type;
		}

		static public Class<?> getClassFromExtension(String extension) {
			for (Extensions fileExt : Extensions.values()) {
				if (fileExt.extension.equals(extension))
					return fileExt.type;
			}
			return null;
		}
	}

	/*
	 * private FileHandle[] getAllFiles() { // handle the asset file FileHandle
	 * handle = Gdx.files.internal("/");
	 * 
	 * Gdx.app.debug(TAG, "Handling " + handle.path()); FileFilter filter = new
	 * FileFilter() {
	 * 
	 * @Override public boolean accept(File pathname) { String fileName =
	 * pathname.getName(); return isFileHandled(fileName); } };
	 * 
	 * FileHandle[] files = handle.list(filter);
	 * 
	 * return files; // handle.extension(); }
	 */
	private String[] getAllLines(FileHandle handle) {
		// Reader reader = handle.reader();
		// InputStream inStream = handle.read();
		BufferedReader buffReader = new BufferedReader(handle.reader());
		Stream<String> lines = buffReader.lines();
		// if (lines.count() == 0)
		// return null;
		String[] out = (String[]) lines.toArray();
		Gdx.app.debug(TAG, out.toString());
		return out;

		/*
		 * Array<String> out = new Array<String>(); try { buffReader.
		 * out.add(buffReader.readLine()); } catch (IOException e) { //
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	private boolean isFileHandled(String fileName) {
		String extension = getExtension(fileName);

		for (Extensions fileExt : Extensions.values()) {
			// Gdx.app.debug(TAG, "checking " + name);
			if (fileExt.extension.equals(extension))
				return fileExt.handled;
		}
		return false;
	}

	private String getExtension(String fileName) {
		String extension;
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1)
			extension = "";
		else
			extension = fileName.substring(dotIndex + 1);
		return extension;
	}

	/*
	 * public void init() { init(new MyAssetManager()); }
	 * 
	 * private class MyAssetManager extends AssetManager{ private
	 * FileHandleResolver resolver;
	 * 
	 * public MyAssetManager(){ this(new InternalFileHandleResolver()); } public
	 * MyAssetManager (FileHandleResolver resolver){ super(resolver);
	 * this.resolver = resolver; } public FileHandleResolver getResolver(){
	 * return resolver; } }
	 * 
	 * FileHandle resolver = null;
	 * 
	 * protected void setResolver(FileHandle resolver) { this.resolver =
	 * resolver; Gdx.app.debug(TAG, "Resolver path: " + resolver.path()); }
	 */

	/**
	 * access files with: assetManager.get(fileName)
	 * 
	 * @param assetManager
	 */
	public void init(AssetManager assetManager) {
		this.saveManager = new SaveManager("assetsMap.json", false);
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);

		// load assets listed in a File
		/*
		 * FileHandle handle = Gdx.files.internal("assets.txt"); if
		 * (handle.exists() || !handle.readString().isEmpty()) { String[] names
		 * = getAllLines(handle); if (names != null) for (String name : names) {
		 * assetManager.load(name, Extensions
		 * .getClassFromExtension(getExtension(name))); Gdx.app.debug(TAG,
		 * "file loaded: " + name); } }
		 */
//		saveManager.saveDataValue("song01", "music/keith303_-_brand_new_highscore.mp3");
		for (Object object : saveManager.getAllData().values()) {
			String name = object.toString();
			assetManager.load(name,
					Extensions.getClassFromExtension(getExtension(name)));
			Gdx.app.debug(TAG, "loade file : " + name);
		}

		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG,
				"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void error(AssetDescriptor filename, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + filename + "'",
				(Exception) throwable);

	}
}