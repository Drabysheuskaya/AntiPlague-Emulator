package view;

import model.HighScore;
import model.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class HighScoreView extends JFrame {
    private JList<String> highScoreList;
    private DefaultListModel<String> listModel;
    private JButton backButton;
    private HighScoreManager highScoreManager;

    public HighScoreView(HighScoreManager highScoreManager) {
        this.highScoreManager = highScoreManager;

        setTitle("High Scores");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Font font = new Font("Arial", Font.PLAIN, 16);
        Font headerFont = new Font("Arial", Font.BOLD, 16);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(1, 4));
        headerPanel.setBackground(Color.LIGHT_GRAY);

        JLabel nameLabel = new JLabel("Name", SwingConstants.CENTER);
        nameLabel.setFont(headerFont);
        JLabel scoreLabel = new JLabel("High Score", SwingConstants.CENTER);
        scoreLabel.setFont(headerFont);
        JLabel difficultyLabel = new JLabel("Difficulty", SwingConstants.CENTER);
        difficultyLabel.setFont(headerFont);
        JLabel timeLabel = new JLabel("Time", SwingConstants.CENTER);
        timeLabel.setFont(headerFont);

        headerPanel.add(nameLabel);
        headerPanel.add(scoreLabel);
        headerPanel.add(difficultyLabel);
        headerPanel.add(timeLabel);


        listModel = new DefaultListModel<>();
        highScoreList = new JList<>(listModel);
        highScoreList.setFont(font);
        highScoreList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(highScoreList);

        backButton = new JButton("Back");
        backButton.setFont(font);


        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        loadHighScores();
    }

    public void setHighScores(List<HighScore> highScores) {
        listModel.clear();
        for (HighScore hs : highScores) {
            String entry = String.format("%-15s | %-10d | %-10s | %ds",
                    hs.getPlayerName(),
                    hs.getScore(),
                    hs.getDifficultyLevel(),
                    hs.getTimeTaken());
            listModel.addElement(entry);
        }
    }

    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    private void loadHighScores() {
        List<HighScore> highScores = highScoreManager.loadHighScores();
        setHighScores(highScores);
    }
}
