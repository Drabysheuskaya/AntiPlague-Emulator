package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Country {
    private String name;
    private Rectangle bounds;
    private float infectionLevel;

    private int totalPopulation;
    private int infectedPopulation;
    private int curedPopulation;

    public int getCuredPopulation() {
        return curedPopulation;
    }

    public void setCuredPopulation(int curedPopulation) {
        this.curedPopulation = curedPopulation;
    }

    public void increaseCuredPopulation(int amount) {
            this.curedPopulation = Math.min(totalPopulation, this.curedPopulation + amount);
    }


    private List<TransportRoute> transportRoutes;

    public Country(String name, int totalPopulation, Rectangle bounds) {
        this.totalPopulation = totalPopulation;
        this.infectedPopulation = 0;
        this.name = name;
        this.bounds = bounds;
        this.infectionLevel = 0;
        this.transportRoutes = new ArrayList<>();
    }

    public int getTotalPopulation() {
        return totalPopulation;
    }


    public int getInfectedPopulation() {
        return infectedPopulation;
    }

    public void setInfectedPopulation(int infectedPopulation) {
        this.infectedPopulation = infectedPopulation;
    }

    public String getName() { return name; }
    public Rectangle getBounds() { return bounds; }
    public float getInfectionLevel() { return infectionLevel; }
    public void setInfectionLevel(float infectionLevel) { this.infectionLevel = infectionLevel; }
    public List<TransportRoute> getTransportRoutes() { return transportRoutes; }
    public void addTransportRoute(TransportRoute route) { transportRoutes.add(route); }
}
