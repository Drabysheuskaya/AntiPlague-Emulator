package model;

import controller.TransportManager;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameModel {
    private List<Country> countries;
    private int score;
    private DifficultyLevel difficultyLevel;
    private boolean isGameOver;
    private int points;
    private int totalCured;
    private List<Country> allCountries;
    private TransportManager transportManager;


    public GameModel() {
        this.countries = new ArrayList<>();
        this.score = 0;
        this.isGameOver = false;
        this.points = 5000;
        this.totalCured = 0;
    }

    public int getTotalCured() {
        return totalCured;
    }

    public void increaseCuredPopulation(Country country, int curedAmount) {
        country.increaseCuredPopulation(curedAmount);
        totalCured += curedAmount;
    }

    public void updateCuredPopulation() {
        int tempCured = 0;
        for(Country country : countries) {
            tempCured += country.getCuredPopulation();
        }
        totalCured = tempCured;

        addPoints((int) (totalCured % 100));
    }


    public void initializeGame(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;

        // Initialize 10 countries with names, populations, and map bounds
        countries.add(new Country("United States", 331_000_000, new Rectangle(230, 175, 125, 75)));
        countries.add(new Country("Canada", 38_000_000, new Rectangle(200, 80, 125, 75)));
        countries.add(new Country("Greenland", 56_000, new Rectangle(425, 50, 125, 75)));
        countries.add(new Country("Brazil", 213_000_000, new Rectangle(325, 475, 125, 75)));
        countries.add(new Country("Algeria", 44_000_000, new Rectangle(575, 175, 125, 75)));
        countries.add(new Country("Sudan", 46_000_000, new Rectangle(750, 375, 125, 75)));
        countries.add(new Country("Russia", 146_000_000, new Rectangle(1025, 50, 125, 75)));
        countries.add(new Country("China", 1_411_000_000, new Rectangle(1100, 200, 125, 75)));
        countries.add(new Country("India", 1_366_000_000, new Rectangle(975, 300, 125, 75)));
        countries.add(new Country("Australia", 25_000_000, new Rectangle(1100, 550, 125, 75)));

        // Transport routes between countries
        Country usa = countries.get(0);
        Country canada = countries.get(1);
        Country greenland = countries.get(2);
        Country brazil = countries.get(3);
        Country algeria = countries.get(4);
        Country sudan = countries.get(5);
        Country russia = countries.get(6);
        Country china = countries.get(7);
        Country india = countries.get(8);
        Country australia = countries.get(9);

        allCountries = List.of(usa, canada, brazil, greenland, russia, china, india, australia, algeria, sudan);


        // Randomly initialize one country with infection
        Random rand = new Random();
        countries.get(rand.nextInt(countries.size())).setInfectedPopulation(10_000);
    }

    public void updateTransportRoutes() {
        transportManager = new TransportManager(allCountries);
        transportManager.createDynamicTransportRoute();
    }


    public List<Country> getCountries() {
        return countries;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public int getPoints() {
        return points;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }

    public boolean applyUpgrade(String upgradeType) {
        switch (upgradeType) {
            case "Vaccine":
                if (points >= 100) {
                    points -= 100;
                    increaseScore(50);
                } else {
                    return false;
                }
                break;
            case "Cure":
                if (points >= 200) {
                    points -= 200;
                    increaseScore(75);
                } else {
                    return false;
                }
                break;
            case "Research":
                if (points >= 300) {
                    points -= 300;
                    increaseScore(100);
                } else {
                    return false;
                }
                break;
            case "Lockdown":
                if (points >= 150) {
                    points -= 150;
                    increaseScore(60);
                } else {
                    return false;
                }
                break;
            case "Travel Ban":
                if (points >= 250) {
                    points -= 250;
                    increaseScore(80);
                } else {
                    return false;
                }
                break;
            case "Isolation":
                if (points >= 300) {
                    points -= 300;
                    increaseScore(90);
                } else {
                    return false;
                }
                break;
            case "Awareness Campaign":
                if (points >= 50) {
                    points -= 50;
                    increaseScore(30);
                } else {
                    return false;
                }
                break;
            case "Antiviral":
                if (points >= 120) {
                    points -= 120;
                    increaseScore(70);
                } else {
                    return false;
                }
                break;
            case "Medical Aid":
                if (points >= 180) {
                    points -= 180;
                    increaseScore(85);
                } else {
                    return false;
                }
                break;
            case "Quarantine":
                if (points >= 350) {
                    points -= 350;
                    increaseScore(120);
                } else {
                    return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }


    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public boolean isGameOver() {
        return isGameOver;
    }


    public boolean isGameWon() {
        for (Country country : countries) {
            if (country.getInfectedPopulation() > 0) {
                return false;
            }
        }
        return true;
    }


    public void updateInfectionSpread(DifficultyLevel difficultyLevel) {
        float spreadMultiplier;

        switch (difficultyLevel) {
            case EASY:
                spreadMultiplier = 0.3f;
                break;
            case HARD:
                spreadMultiplier = 0.7f;
                break;
            case MEDIUM:
            default:
                spreadMultiplier = 0.5f;
                break;
        }

        for (Country country : countries) {
            if (country.getInfectedPopulation() > 0) {
                float infectionLevel = country.getInfectionLevel() / country.getInfectedPopulation();
                country.setInfectionLevel(infectionLevel);
                for (TransportRoute route : country.getTransportRoutes()) {
                    Country targetCountry = route.getDestination();
                    int spread = (int) (country.getInfectedPopulation() * spreadMultiplier);
                    targetCountry.setInfectedPopulation(
                            Math.min(targetCountry.getTotalPopulation(),
                                    targetCountry.getInfectedPopulation() + spread)
                    );
                }
            }

            increaseCuredPopulation(country, (int) (country.getInfectedPopulation() * spreadMultiplier) / 3);
        }

        boolean allCountriesDisinfected = countries.stream().allMatch(c -> c.getInfectedPopulation() <= 0);
        if (allCountriesDisinfected) {
            isGameOver = true;
        }

        boolean allCountriesFullyInfected = countries.stream().allMatch(c -> c.getInfectedPopulation() == c.getTotalPopulation());
        if (allCountriesFullyInfected) {
            isGameOver = true;
        }
    }


}