package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends JPanel {
    private BufferedImage worldMap;
    private List<Country> countries;
    private JLabel scoreLabel;
    private JLabel pointsLabel ;
    private JLabel timeLabel;
    private long time;
    private float pulseScale = 1.0f;
    private boolean pulseGrowing = true;
    private boolean flashVisible = true;
    private boolean isGameWonDisplayed = false;
    private List<TransportAnimation> transportAnimations = new ArrayList<>();
    private Timer randomTransportTimer;
    private BufferedImage shipImage, planeImage, carImage;
    private List<Line> infectedLines = new ArrayList<>();

    private JButton[] upgradeButtons = new JButton[10];

    private GameModel gameModel;
    private HighScoreManager highScoreManager;

    public GameView(List<Country> countries, GameModel gameModel) {
        this.countries = countries;
        this.gameModel = gameModel;
        highScoreManager = new HighScoreManager();

        this.setLayout(new BorderLayout(10, 10));

        pointsLabel = new JLabel("Upgrade Points: " + gameModel.getPoints());
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 16));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        timeLabel = new JLabel("Time: 0");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));


        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(163, 138, 191, 137));

        Font spacerFont = new Font("Arial", Font.BOLD, 16);
        JLabel spacer1 = new JLabel("  (");
        spacer1.setFont(spacerFont);
        JLabel spacer2 = new JLabel(")  ");
        spacer2.setFont(spacerFont);

        infoPanel.add(scoreLabel);
        infoPanel.add(spacer1);
        infoPanel.add(pointsLabel);
        infoPanel.add(spacer2);
        infoPanel.add(timeLabel);

        this.add(infoPanel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(20, 0, 0, 3));
        buttonsPanel.setBackground(new Color(43, 33, 39, 180));

        JLabel upgradesLabel;
        upgradesLabel = new JLabel("Upgrades");
        upgradesLabel.setForeground(new Color(41, 227, 120, 180));
        upgradesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        buttonsPanel.add(upgradesLabel);

        for (int i = 0; i < 10; i++) {
            String upgradeName = getUpgradeName(i);
            upgradeButtons[i] = new JButton(upgradeName);
            final int index = i;
            upgradeButtons[i].setFont(new Font("Arial", Font.BOLD, 16));

            if (i % 2 == 0) {
                upgradeButtons[i].setForeground(new Color(27, 169, 212, 180));
            } else {
                upgradeButtons[i].setForeground(new Color(17, 111, 138, 180));
            }

            upgradeButtons[i].addActionListener(e -> handleUpgradeClick(index));
            buttonsPanel.add(upgradeButtons[i]);
        }


        this.add(buttonsPanel, BorderLayout.WEST);

        Timer animationTimer = new Timer(100, e -> updateAnimations());
        animationTimer.start();

        try {
            URL resourceURL = getClass().getClassLoader().getResource("worldmap.jpg");

            if (resourceURL == null) {
                throw new IOException("Resource not found: worldmap.jpg");
            }

            try (InputStream is = resourceURL.openStream()) {
                worldMap = ImageIO.read(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            shipImage = ImageIO.read(getClass().getResource("/ship.png"));
            planeImage = ImageIO.read(getClass().getResource("/plane.png"));
            carImage = ImageIO.read(getClass().getResource("/car.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        randomTransportTimer = new Timer(2000, e -> {
            triggerRandomTransportAnimations();
        });


        randomTransportTimer.start();

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    saveScoreAndExit("Game Closed");
                }
            });
        }

    }

    private String getUpgradeName(int index) {
        switch (index) {
            case 0: return "Vaccine";
            case 1: return "Cure";
            case 2: return "Research";
            case 3: return "Lockdown";
            case 4: return "Travel Ban";
            case 5: return "Isolation";
            case 6: return "Awareness Campaign";
            case 7: return "Antiviral";
            case 8: return "Medical Aid";
            case 9: return "Quarantine";
            default: return "Upgrade " + (index + 1);
        }
    }

    private void handleUpgradeClick(int index) {
        String upgradeType = getUpgradeName(index);
        boolean upgradeSuccess = gameModel.applyUpgrade(upgradeType);

        if(upgradeSuccess){
            scoreLabel.setText("Score: " + gameModel.getScore());

            for (Country country : countries) {
                int infectedPopulation = country.getInfectedPopulation();
                if (infectedPopulation > 0) {
                    int reducedPopulation = (int) (infectedPopulation * 0.7);
                    country.setInfectedPopulation(reducedPopulation);
                    country.increaseCuredPopulation(infectedPopulation- reducedPopulation);
                }
            }
            pointsLabel.setText("Upgrade Points: " + gameModel.getPoints());
        } else {
            JOptionPane.showMessageDialog(this, "Upgrade not applied. Low upgrade points.", "Low upgrade points",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (worldMap != null) {
            g.drawImage(worldMap, 0, 0, getWidth(), getHeight(), this);
        }
        drawCountries(g);
        drawDashboard(g);


        g.setColor(Color.RED);
        for (Line infectedLine : infectedLines) {
            g.drawLine(
                    infectedLine.getStartBounds().x + infectedLine.getStartBounds().width / 2,
                    infectedLine.getStartBounds().y + infectedLine.getStartBounds().height / 2,
                    infectedLine.getEndBounds().x + infectedLine.getEndBounds().width / 2,
                    infectedLine.getEndBounds().y + infectedLine.getEndBounds().height / 2
            );
        }

        for (int i = 0; i < transportAnimations.size(); i++) {
            TransportAnimation animation = transportAnimations.get(i);
            animation.update();
            animation.draw(g);

            if (animation.isInfected()) {
                drawInfectedLine(g, animation.getBounds(), animation.getBounds());
            }

            if (animation.isCompleted()) {
                transportAnimations.remove(i);
                i--;
            }
        }
    }

    private void saveScoreAndExit(String status) {
        String playerName = JOptionPane.showInputDialog(this, "Enter your name:", "Save Score",
                JOptionPane.PLAIN_MESSAGE);
        if (playerName != null && !playerName.trim().isEmpty()) {
            int score = gameModel.getScore();
            DifficultyLevel difficultyLevel = gameModel.getDifficultyLevel();
            String timeText = timeLabel.getText().replace("Time: ", "");
            int timeTaken = Integer.parseInt(timeText);

            HighScore highScore = new HighScore(playerName, score, difficultyLevel, timeTaken);
            highScoreManager.addHighScore(highScore);

            JOptionPane.showMessageDialog(this, "Score saved successfully!", "High Score",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Score not saved!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        System.exit(0);
    }


    private void drawCountries(Graphics g) {
        for (Country country : countries) {
            Rectangle bounds = country.getBounds();

            g.setColor(new Color(50, 50, 100, 175));
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            g.setColor(Color.GRAY);
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

            if (country.getInfectedPopulation() > 0) {
                int circleDiameter = Math.min(bounds.width, bounds.height) / 3;
                int scaledDiameter = (int) (circleDiameter * pulseScale);
                int circleX = bounds.x + bounds.width - scaledDiameter - 5;
                int circleY = bounds.y + bounds.height - scaledDiameter - 5;
                g.setColor(Color.RED);
                g.fillOval(circleX, circleY, scaledDiameter, scaledDiameter);
            } else {
                int circleDiameter = Math.min(bounds.width, bounds.height) / 3;
                int circleX = bounds.x + bounds.width - circleDiameter - 5;
                int circleY = bounds.y + bounds.height - circleDiameter - 5;
                g.setColor(Color.GREEN);
                g.fillOval(circleX, circleY, circleDiameter, circleDiameter);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            int textX = bounds.x + 5;
            int textY = bounds.y + 15;

            g.drawString(country.getName(), textX, textY);
            g.drawString("Infected: " + country.getInfectedPopulation(), textX, textY + 15);
            g.drawString("Total: " + country.getTotalPopulation(), textX, textY + 30);
            g.drawString("Cured: " + country.getCuredPopulation(), textX, textY + 45);
        }
    }

    private void drawDashboard(Graphics g) {
        int dashboardWidth = getWidth() / 4;
        int dashboardHeight = 150;
        int dashboardX = (getWidth() - dashboardWidth) / 2;
        int dashboardY = getHeight() - dashboardHeight - 20;

        g.setColor(new Color(45, 25, 0, 225));
        g.fillRoundRect(dashboardX, dashboardY, dashboardWidth, dashboardHeight, 20, 20);

        g.setColor(new Color(168, 140, 93));
        g.setFont(new Font("Arial", Font.BOLD, 14));

        int totalCountries = countries.size();
        int infectedCountries = (int) countries.stream().filter(c -> c.getInfectedPopulation() > 0).count();
        long totalPopulation = countries.stream().mapToLong(Country::getTotalPopulation).sum();
        long infectedPopulation = countries.stream().mapToLong(Country::getInfectedPopulation).sum();
        double infectionLevel = (totalPopulation > 0) ? (100.0 * infectedPopulation / totalPopulation) : 0.0;

        int textX = dashboardX + 20;
        int textY = dashboardY + 30;
        g.drawString("Total countries: " + totalCountries, textX, textY-10);
        if (flashVisible) {
            g.setColor(Color.RED);
        }
        g.drawString("Infected countries: " + infectedCountries, textX, textY + 10);
        g.drawString("Infected population: " + infectedPopulation, textX, textY + 50);
        g.setColor(new Color(168, 140, 93));
        g.drawString("Total population: " + totalPopulation, textX, textY + 30);
        g.drawString("Infection level: " + String.format("%.2f", infectionLevel) + "%", textX, textY + 70);
        g.drawString("Total cured: " + gameModel.getTotalCured(), textX, textY + 90);
        g.drawString("Current Time: " + getCurrentTime(), textX, textY + 110);
    }

    private String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.now().format(formatter);
    }

    private void addTransportAnimation(Country startCountry, Country endCountry, TransportType transportType, boolean isInfected) {
        BufferedImage transportImage = null;
        switch (transportType) {
            case LAND:
                transportImage = carImage;
                break;
            case SEA:
                transportImage = shipImage;
                break;
            case AIR:
                transportImage = planeImage;
                break;
        }

        if (transportImage != null) {
            TransportAnimation animation = new TransportAnimation(
                    transportImage,
                    startCountry.getBounds(),
                    endCountry.getBounds(),
                    isInfected
            );

            transportAnimations.add(animation);

            if (isInfected) {
                infectedLines.add(new Line(
                        startCountry.getBounds(),
                        endCountry.getBounds()
                ));
            }
        }
    }



    private void drawInfectedLine(Graphics g, Rectangle startBounds, Rectangle endBounds) {
        g.setColor(Color.RED);
        g.drawLine(startBounds.x + startBounds.width / 2, startBounds.y + startBounds.height / 2,
                endBounds.x + endBounds.width / 2, endBounds.y + endBounds.height / 2);
    }


    private void triggerRandomTransportAnimations() {
        Random random = new Random();
        List<Country> countryList = gameModel.getCountries();

        if (countryList.isEmpty()) {
            return;
        }

        int randomIndex = random.nextInt(countryList.size());
        Country startCountry = countryList.get(randomIndex);

        List<TransportRoute> transportRoutes = startCountry.getTransportRoutes();
        if (transportRoutes.isEmpty()) {
            return;
        }

        TransportRoute randomRoute = transportRoutes.get(random.nextInt(transportRoutes.size()));
        Country endCountry = randomRoute.getDestination();
        TransportType transportType = randomRoute.getTransportType();

        boolean isInfected = false;
        if(startCountry.getInfectedPopulation() > 0)
            isInfected = true;

        addTransportAnimation(startCountry, endCountry, transportType, isInfected);
    }





    private void updateAnimations() {
        if (pulseGrowing) {
            pulseScale += 0.05f;
            if (pulseScale >= 1.3f) {
                pulseGrowing = false;
            }
        } else {
            pulseScale -= 0.05f;
            if (pulseScale <= 1.0f) {
                pulseGrowing = true;
            }
        }

        flashVisible = !flashVisible;
        repaint();
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateUpgradePoints(int points) {
        pointsLabel.setText("Upgrade Points: " + points);
    }

    public void updateTime(long time) {
        this.time = time;
        timeLabel.setText("Time: " + time);
    }
}
