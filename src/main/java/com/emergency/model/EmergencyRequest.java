package com.emergency.model;

import com.emergency.model.Enums.*;

public class EmergencyRequest implements Comparable<EmergencyRequest> {
    private final String patientId;
    private final Priority priority;
    private final Location pickupLocation;
    private final Location destinationHospital;
    private Ambulance assignedAmbulance;

    public EmergencyRequest(String patientId, Priority priority, Location pickupLocation, Location destinationHospital) {
        this.patientId = patientId;
        this.priority = priority;
        this.pickupLocation = pickupLocation;
        this.destinationHospital = destinationHospital;
    }

    // High priority values come first in the queue
    @Override
    public int compareTo(EmergencyRequest other) {
        return this.priority.ordinal() - other.priority.ordinal();
    }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public Priority getPriority() { return priority; }
    public Location getPickupLocation() { return pickupLocation; }
    public Ambulance getAssignedAmbulance() { return assignedAmbulance; }
    public void setAssignedAmbulance(Ambulance ambulance) { this.assignedAmbulance = ambulance; }
}
