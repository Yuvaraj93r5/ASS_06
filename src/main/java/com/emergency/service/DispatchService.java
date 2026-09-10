package com.emergency.service;

import com.emergency.model.*;
import com.emergency.model.Enums.*;
import com.emergency.exception.*;

import java.util.*;

public class DispatchService {
    private final List<Ambulance> fleet = new ArrayList<>();
    private final PriorityQueue<EmergencyRequest> waitingQueue = new PriorityQueue<>();
    private final List<EmergencyRequest> history = new ArrayList<>();

    public void registerAmbulance(Ambulance ambulance) {
        fleet.add(ambulance);
    }

    public synchronized void submitEmergencyRequest(EmergencyRequest request) throws NoAmbulanceAvailableException {
        if (request.getPatientId() == null || request.getPickupLocation() == null) {
            throw new InvalidRequestException("Invalid emergency request data provided.");
        }

        history.add(request);
        Optional<Ambulance> bestAmbulance = findBestAvailableAmbulance(request);

        if (bestAmbulance.isPresent()) {
            dispatch(request, bestAmbulance.get());
        } else {
            waitingQueue.add(request);
            throw new NoAmbulanceAvailableException("No available fleet units. Placed in priority queue.");
        }
    }

    private Optional<Ambulance> findBestAvailableAmbulance(EmergencyRequest request) {
        return fleet.stream()
                .filter(a -> a.getStatus() == AmbulanceStatus.AVAILABLE)
                .filter(a -> matchesTypeRequirements(a.getType(), request.getPriority()))
                .min(Comparator.comparingDouble(a -> a.getCurrentLocation().distanceTo(request.getPickupLocation())));
    }

    private boolean matchesTypeRequirements(AmbulanceType type, Priority priority) {
        if (priority == Priority.CRITICAL) return type == AmbulanceType.ICU;
        if (priority == Priority.HIGH) return type == AmbulanceType.ADVANCED_LIFE_SUPPORT || type == AmbulanceType.ICU;
        return true;
    }

    private void dispatch(EmergencyRequest request, Ambulance ambulance) {
        ambulance.setStatus(AmbulanceStatus.DISPATCHED);
        request.setAssignedAmbulance(ambulance);
    }

    public synchronized void updateAmbulanceStatus(String ambulanceId, AmbulanceStatus newStatus, Location updatedLocation) {
        Ambulance ambulance = fleet.stream()
                .filter(a -> a.getId().equals(ambulanceId))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Ambulance ID not found."));

        ambulance.setStatus(newStatus);
        if (updatedLocation != null) {
            ambulance.setCurrentLocation(updatedLocation);
        }

        // If returned to available state, check the waiting queue immediately
        if (newStatus == AmbulanceStatus.AVAILABLE && !waitingQueue.isEmpty()) {
            EmergencyRequest nextRequest = waitingQueue.poll();
            dispatch(nextRequest, ambulance);
        }
    }

    public double calculateETA(Ambulance ambulance, Location pickupLocation) {
        double distance = ambulance.getCurrentLocation().distanceTo(pickupLocation);
        double averageSpeedKmH = 50.0; 
        return (distance / averageSpeedKmH) * 60; // Returns ETA in minutes
    }

    public List<EmergencyRequest> getHistory() { return history; }
    public PriorityQueue<EmergencyRequest> getWaitingQueue() { return waitingQueue; }
}
