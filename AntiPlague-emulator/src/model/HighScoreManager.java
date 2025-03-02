package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.dat";
    private List<HighScore> highScores;

    public HighScoreManager() {
        highScores = loadHighScores();
    }

    public void addHighScore(HighScore highScore) {
        highScores.add(highScore);
        saveHighScores();
    }

    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }

    public List<HighScore> loadHighScores() {
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            return (List<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
