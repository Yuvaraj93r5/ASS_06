package com.emergency.service;

import com.emergency.model.*;
import com.emergency.model.Enums.*;
import com.emergency.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DispatchServiceTest {
    private DispatchService service;

    @BeforeEach
    void setUp() {
        service = new DispatchService();
    }

    @Test
    void testSuccessfulImmediateDispatchBasedOnProximity() throws Exception {
        Ambulance farBasic = new Ambulance("A1", AmbulanceType.BASIC, "Driver 1", new Location(0, 10));
        Ambulance closeBasic = new Ambulance("A2", AmbulanceType.BASIC, "Driver 2", new Location(0, 2));
        service.registerAmbulance(farBasic);
        service.registerAmbulance(closeBasic);

        EmergencyRequest request = new EmergencyRequest("P100", Priority.NORMAL, new Location(0, 0), new Location(0, 5));
        service.submitEmergencyRequest(request);

        assertEquals("A2", request.getAssignedAmbulance().getId());
        assertEquals(AmbulanceStatus.DISPATCHED, closeBasic.getStatus());
    }

    @Test
    void testQueueAndAutoAllocationWhenFleetIsExhausted() {
        Ambulance amb = new Ambulance("A1", AmbulanceType.ICU, "Driver 1", new Location(0, 0));
        service.registerAmbulance(amb);

        EmergencyRequest req1 = new EmergencyRequest("P1", Priority.CRITICAL, new Location(0, 1), new Location(0, 5));
        EmergencyRequest req2 = new EmergencyRequest("P2", Priority.CRITICAL, new Location(0, 2), new Location(0, 6));

        assertDoesNotThrow(() -> service.submitEmergencyRequest(req1));
        assertThrows(NoAmbulanceAvailableException.class, () -> service.submitEmergencyRequest(req2));

        assertEquals(1, service.getWaitingQueue().size());

        // Free up the ambulance
        service.updateAmbulanceStatus("A1", AmbulanceStatus.AVAILABLE, new Location(0, 5));

        // Verify auto-allocation took place
        assertEquals(AmbulanceStatus.DISPATCHED, amb.getStatus());
        assertNotNull(req2.getAssignedAmbulance());
        assertEquals("A1", req2.getAssignedAmbulance().getId());
    }

    @Test
    void testInvalidRequestExceptionThrown() {
        EmergencyRequest invalidReq = new EmergencyRequest(null, Priority.NORMAL, null, null);
        assertThrows(InvalidRequestException.class, () -> service.submitEmergencyRequest(invalidReq));
    }
}
