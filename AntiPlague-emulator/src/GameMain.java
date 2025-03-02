import controller.MainMenuController;
import view.MainMenuView;

import javax.swing.*;

public class GameMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainMenuView mainMenuView = new MainMenuView();
            new MainMenuController(mainMenuView);
            mainMenuView.setVisible(true);
        });
    }
}