package controller;

import model.Country;
import model.GameModel;
import model.HighScore;
import model.HighScoreManager;
import view.GameView;
import view.MainMenuView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private GameModel model;
    private GameView view;
    private Timer gameTimer;
    private Timer transportRouteTimer;
    private long startTime;
    private JFrame frame;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        this.startTime = System.currentTimeMillis();
        startGameLoop();

        frame = new JFrame("AntiPlague Coronavirus Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.setSize(1400, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    gameTimer.cancel();
                    transportRouteTimer.cancel();
                    SwingUtilities.invokeLater(() -> {
                        frame.dispose();
                        MainMenuView mainMenuView = new MainMenuView();
                        new MainMenuController(mainMenuView);
                        mainMenuView.setVisible(true);
                    });
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        // Ctrl+Shift+Q
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "exitToMainMenu");
        actionMap.put("exitToMainMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu(frame);
            }
        });

    }

    private void returnToMainMenu(JFrame gameFrame) {
        int confirm = JOptionPane.showConfirmDialog(
                gameFrame,
                "Are you sure you want to exit to the main menu?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            gameTimer.cancel();
            transportRouteTimer.cancel();
            gameFrame.dispose();
            SwingUtilities.invokeLater(() -> {
                MainMenuView mainMenuView = new MainMenuView();
                new MainMenuController(mainMenuView);
                mainMenuView.setVisible(true);
            });
        }
    }

    private void startGameLoop() {
        // Initial transport route update
        model.updateTransportRoutes();

        // Start game loop with a 1-second interval
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGame();
            }
        }, 0, 1000);

        // Update transport routes every 5 seconds
        transportRouteTimer = new Timer();
        transportRouteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                model.updateTransportRoutes();
            }
        }, 0, 5000);  // Update every 5000 ms (5 seconds)
    }

    private void updateGame() {
        if (model.isGameOver()) {
            gameTimer.cancel();
            transportRouteTimer.cancel();

            int totalInfected = model.getCountries().stream().mapToInt(Country::getInfectedPopulation).sum();
            if (totalInfected == 0) {
                winGame();
            } else {
                loseGame();
            }
            return;
        }

        model.updateInfectionSpread(model.getDifficultyLevel());
        model.updateCuredPopulation();

        int totalInfected = 0;
        int totalPopulation = 0;

        for (Country country : model.getCountries()) {
            totalInfected += country.getInfectedPopulation();
            totalPopulation += country.getTotalPopulation();
        }

        view.repaint();
        view.updateScore(model.getScore());
        view.updateUpgradePoints(model.getPoints());

        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        view.updateTime(elapsedTime);

        int multiplier = 0;

        switch (model.getDifficultyLevel()) {
            case EASY:
                multiplier = 1;
                break;
            case HARD:
                multiplier = 3;
                break;
            case MEDIUM:
            default:
                multiplier = 2;
                break;
        }
        model.increaseScore((int)elapsedTime*multiplier);
        model.addPoints((int)elapsedTime*multiplier + (int)elapsedTime);
    }

    private void winGame() {
        String playerName = JOptionPane.showInputDialog("Congratulations, you win! Enter your name:");
        if (playerName != null && !playerName.trim().isEmpty()) {
            HighScore highScore = new HighScore(playerName.trim(), model.getScore(), model.getDifficultyLevel(), (System.currentTimeMillis() - startTime) / 1000);
            HighScoreManager highScoreManager = new HighScoreManager();
            highScoreManager.addHighScore(highScore);
        }

        returnToMainMenu(frame);
    }

    private void loseGame() {
        JOptionPane.showMessageDialog(null, "More than 70% of the population is infected. You have lost the game!");

        String playerName = JOptionPane.showInputDialog("Enter your name to save your score:");
        if (playerName != null && !playerName.trim().isEmpty()) {
            HighScore highScore = new HighScore(playerName.trim(), model.getScore(), model.getDifficultyLevel(), (System.currentTimeMillis() - startTime) / 1000);
            HighScoreManager highScoreManager = new HighScoreManager();
            highScoreManager.addHighScore(highScore);
        }

        SwingUtilities.invokeLater(() -> {
            MainMenuView mainMenuView = new MainMenuView();
            new MainMenuController(mainMenuView);
            mainMenuView.setVisible(true);
        });
    }
}
