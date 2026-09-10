package com.emergency.model;

import com.emergency.model.Enums.*;

public class Ambulance {
    private final String id;
    private final AmbulanceType type;
    private final String driverDetails;
    private AmbulanceStatus status;
    private Location currentLocation;

    public Ambulance(String id, AmbulanceType type, String driverDetails, Location currentLocation) {
        this.id = id;
        this.type = type;
        this.driverDetails = driverDetails;
        this.status = AmbulanceStatus.AVAILABLE;
        this.currentLocation = currentLocation;
    }

    // Getters and Setters
    public String getId() { return id; }
    public AmbulanceType getType() { return type; }
    public AmbulanceStatus getStatus() { return status; }
    public void setStatus(AmbulanceStatus status) { this.status = status; }
    public Location getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(Location location) { this.currentLocation = location; }
    public String getDriverDetails() { return driverDetails; }
}
