package model;

public class TransportRoute {
    private Country origin;
    private Country destination;
    private TransportType type;
    private boolean isOpen;

    public TransportRoute(Country origin, Country destination, TransportType type) {
        this.origin = origin;
        this.destination = destination;
        this.type = type;
        this.isOpen = true;
    }
    public TransportType getTransportType() {
        return type;
    }

    public Country getDestination() { return destination; }
}
