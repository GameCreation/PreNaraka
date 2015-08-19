package de.gamecreation.prenaraka.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;

public class SaveManager {
	private static final String TAG = SaveManager.class.getName();

	private boolean encoded;
	private Save save = getSave();

	public SaveManager(String filePath, boolean encoded) {
		this.file  = Gdx.files.local(filePath);//local,internal
		Gdx.app.debug(TAG, "filePath: " + file.path());
		this.encoded = encoded;
		save = getSave();
	}

	public static class Save {
		public ObjectMap<String, Object> data = new ObjectMap<String, Object>();
	}

	private FileHandle file;

	private Save getSave() {
		Save save = new Save();

		if (file != null && file.exists()) {
			Json json = new Json();
			if (encoded)
				save = json.fromJson(Save.class,
						Base64Coder.decodeString(file.readString()));
			else
				save = json.fromJson(Save.class, file.readString());
		}
		return save;
	}

	public void saveToJson() {
		Json json = new Json();
		json.setOutputType(OutputType.json);
		if (encoded)
			file.writeString(Base64Coder.encodeString(json.prettyPrint(save)),
					false);
		else
			file.writeString(json.prettyPrint(save), false);
	}

	@SuppressWarnings("unchecked")
	public <T> T loadDataValue(String key, Class<T> type) {
		if (save.data.containsKey(key))
			return (T) save.data.get(key);
		else
			return null; // this if() avoids and exception, but check for null
							// on load.
	}

	public void saveDataValue(String key, Object object) {
		save.data.put(key, object);
		saveToJson(); // Saves current save immediatly.
	}

	public ObjectMap<String, Object> getAllData() {
		return save.data;
	}
}