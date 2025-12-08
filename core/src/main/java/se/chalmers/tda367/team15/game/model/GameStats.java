package se.chalmers.tda367.team15.game.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class GameStats {

    private static final String SAVE_FOLDER = "saves";
    private static final String STATS_FILE = "gamestats.json";
    private static final String DAYS_KEY = "daysSurvived";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private int daysSurvived;

    public GameStats(int daysSurvived) {
        this.daysSurvived = daysSurvived;
    }

    public void saveIfNewHighScore() {
        int currentHighScore = loadHighScore();
        if (daysSurvived > currentHighScore) {
            save(daysSurvived);
        }
    }

    public static int loadHighScore() {
        Path filePath = getFilePath();

        if (!Files.exists(filePath)) {
            return 0;
        }

        try {
            String json = Files.readString(filePath);
            JsonObject root = GSON.fromJson(json, JsonObject.class);

            if (root != null && root.has(DAYS_KEY)) {
                return root.get(DAYS_KEY).getAsInt();
            }

        } catch (IOException e) {
            System.err.println("Failed to load high score: " + e.getMessage());
        }

        return 0;
    }

    private void save(int highScore) {
        Path folderPath = Paths.get(SAVE_FOLDER);
        Path filePath = getFilePath();

        try {
            Files.createDirectories(folderPath);

            JsonObject json = new JsonObject();
            json.addProperty(DAYS_KEY, highScore);

            Files.writeString(filePath, GSON.toJson(json));

        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }

    private static Path getFilePath() {
        return Paths.get(SAVE_FOLDER, STATS_FILE);
    }
}
