package model;

import java.io.Serializable;

public class HighScore implements Serializable {
    private String playerName;
    private int score;
    private DifficultyLevel difficultyLevel;
    private long timeTaken;

    public HighScore(String playerName, int score, DifficultyLevel difficultyLevel, long timeTaken) {
        this.playerName = playerName;
        this.score = score;
        this.difficultyLevel = difficultyLevel;
        this.timeTaken = timeTaken;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public long getTimeTaken() {
        return timeTaken;
    }
}
