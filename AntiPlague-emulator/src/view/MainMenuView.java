package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuView extends JFrame {
    private JButton newGameButton;
    private JButton highScoresButton;
    private JButton exitButton;

    public MainMenuView() {
        setTitle("Plague Inc. - Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        Font buttonFont = new Font("Arial", Font.BOLD, 18);

        newGameButton = createStyledButton("New Game", buttonFont);
        highScoresButton = createStyledButton("High Scores", buttonFont);
        exitButton = createStyledButton("Exit", buttonFont);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(newGameButton, gbc);

        gbc.gridy = 1;
        add(highScoresButton, gbc);

        gbc.gridy = 2;
        add(exitButton, gbc);
    }

    private JButton createStyledButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(new Color(164, 107, 23));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(30, 144, 255));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
        });

        return button;
    }

    public void addNewGameListener(ActionListener listener) {
        newGameButton.addActionListener(listener);
    }

    public void addHighScoresListener(ActionListener listener) {
        highScoresButton.addActionListener(listener);
    }

    public void addExitListener(ActionListener listener) {
        exitButton.addActionListener(listener);
    }
}