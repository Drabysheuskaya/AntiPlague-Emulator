package controller;

import model.DifficultyLevel;
import model.GameModel;
import model.HighScoreManager;
import view.GameView;
import view.HighScoreView;
import view.MainMenuView;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuController {
    private MainMenuView view;

    public MainMenuController(MainMenuView view) {
        this.view = view;
        this.view.addNewGameListener(new NewGameListener());
        this.view.addHighScoresListener(new HighScoresListener());
        this.view.addExitListener(new ExitListener());
    }

    class NewGameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            DifficultyLevel difficulty = selectDifficulty();
            GameModel model = new GameModel();
            model.initializeGame(difficulty);
            GameView gameView = new GameView(model.getCountries(), model);
            new GameController(model, gameView);
            view.dispose();
        }

        private DifficultyLevel selectDifficulty() {
            String[] options = {"Easy", "Medium", "Hard"};

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Select Difficulty Level\n (Default = Medium)\n",
                    "Difficulty Selection",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0: return DifficultyLevel.EASY;
                case 1: return DifficultyLevel.MEDIUM;
                case 2: return DifficultyLevel.HARD;
                default: return DifficultyLevel.MEDIUM;
            }
        }
    }

    class HighScoresListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            HighScoreManager highScoreManager = new HighScoreManager();
            HighScoreView highScoreView = new HighScoreView(highScoreManager);
            highScoreView.addBackListener(new BackListener());
            highScoreView.setVisible(true);
            view.dispose();
        }
    }

    class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    class BackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainMenuView mainMenuView = new MainMenuView();
            new MainMenuController(mainMenuView);
            mainMenuView.setVisible(true);
            ((JFrame) ((JButton) e.getSource()).getTopLevelAncestor()).dispose();
        }
    }
}
