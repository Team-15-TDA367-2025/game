package se.chalmers.tda367.team15.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.HashMap;
import java.util.Map;

public class GameStats {
    private static final String SAVE_FOLDER = "saves/";
    private static final String STATS_FILE = SAVE_FOLDER + "gamestats.json";
    private static final String DAYS_KEY = "daysSurvived";

    private int daysSurvived;

    private static final Json JSON = new Json();

    public GameStats(int daysSurvived) {
        this.daysSurvived = 6;
        JSON.setOutputType(JsonWriter.OutputType.json);
    }

    public void saveIfNewHighScore() {
        int currentHighScore = loadHighScore();

        if (this.daysSurvived > currentHighScore) {
            save(this.daysSurvived);
        }
    }

    public static int loadHighScore() {
        FileHandle file = Gdx.files.external(STATS_FILE);

        if (!file.exists()) {
            return 0;
        }

        try {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(file.readString());

            if (root != null && root.has(DAYS_KEY)) {
                return root.getInt(DAYS_KEY);
            }
        } catch (Exception e) {
            Gdx.app.error("GameStats", "Error loading high score from file: " + STATS_FILE, e);
        }
        return 0;
    }

    private void save(int highScore) {

        Gdx.files.local(SAVE_FOLDER).mkdirs();

        FileHandle file = Gdx.files.local(STATS_FILE);

        Map<String, Integer> data = new HashMap<>();
        data.put(DAYS_KEY, highScore);

        String jsonString = JSON.toJson(data);
        file.writeString(jsonString, false);
    }
}