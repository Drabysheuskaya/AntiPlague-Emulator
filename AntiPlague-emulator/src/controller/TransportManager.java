package controller;

import model.Country;
import model.TransportRoute;
import model.TransportType;

import java.util.List;
import java.util.Random;

public class TransportManager {

    private Random random = new Random();
    private List<Country> allCountries;

    public TransportManager(List<Country> countries) {
        this.allCountries = countries;
    }

    public void createDynamicTransportRoute() {
        Country source = getRandomCountry();
        Country target = getRandomCountry(source);

        TransportRoute route = createTransportRoute(source, target);
        if (route != null) {
            source.addTransportRoute(route);
        }
    }

    private Country getRandomCountry() {
        return allCountries.get(random.nextInt(allCountries.size()));
    }

    private Country getRandomCountry(Country exclude) {
        Country randomCountry;
        do {
            randomCountry = getRandomCountry();
        } while (randomCountry.equals(exclude));
        return randomCountry;
    }

    private TransportRoute createTransportRoute(Country source, Country target) {
        if (shouldCreateRoute(source, target)) {
            return new TransportRoute(source, target, getRandomTransportType());
        }
        return null;
    }

    private boolean shouldCreateRoute(Country source, Country target) {
        if (source.getInfectedPopulation() == 0 || target.getInfectedPopulation() == 0) {
            return true;
        }

        return source.getInfectionLevel() > target.getInfectionLevel();
    }

    private TransportType getRandomTransportType() {
        TransportType[] transportTypes = TransportType.values();
        return transportTypes[random.nextInt(transportTypes.length)];
    }
}
