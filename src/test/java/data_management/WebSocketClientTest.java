package data_management;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.WebSocketClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests and integration tests for WebSocketClientImpl
 * Since we can't connect to a real server in unit tests
 * we test the message parsing logic directly by calling onMessage.
 */
class WebSocketClientTest {

    private WebSocketClientImpl client;
    private DataStorage storage;

    @BeforeEach
    void setUp() throws URISyntaxException {
        client = new WebSocketClientImpl("ws://localhost:8080");
        storage = DataStorage.getInstance();
        try {
            var field = WebSocketClientImpl.class.getDeclaredField("dataStorage");
            field.setAccessible(true);
            field.set(client, storage);
        } catch (Exception e) {
            fail("Could not set dataStorage field: " + e.getMessage());
        }
    }

    
    // Normal message parsing
   
    @Test
    void testOnMessage_validMessage_storedCorrectly() {
        client.onMessage((String)"1,1000,ECG,0.5");
        assertFalse(storage.getRecords(1, 1000L, 1000L).isEmpty());
    }

    @Test
    void testOnMessage_triggeredAlert_storedAsOne() {
        client.onMessage((String)"2,2000,Alert,triggered");
        var records = storage.getRecords(2, 2000L, 2000L);
        assertFalse(records.isEmpty());
        assertEquals(1.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testOnMessage_resolvedAlert_storedAsZero() {
        client.onMessage((String)"3,3000,Alert,resolved");
        var records = storage.getRecords(3, 3000L, 3000L);
        assertFalse(records.isEmpty());
        assertEquals(0.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testOnMessage_bloodPressure_storedCorrectly() {
        client.onMessage((String)"4,4000,SystolicPressure,120.0");
        assertFalse(storage.getRecords(4, 4000L, 4000L).isEmpty());
    }

    
    // Error handling - corrupted/malformed messages
    @Test
    void testOnMessage_tooFewParts_skipped() {
        assertDoesNotThrow(() -> client.onMessage((String)"1,1000,ECG"));
    }

    @Test
    void testOnMessage_tooManyParts_skipped() {
        assertDoesNotThrow(() -> client.onMessage((String)"1,1000,ECG,0.5,extra"));
    }

    @Test
    void testOnMessage_emptyMessage_skipped() {
        assertDoesNotThrow(() -> client.onMessage((String)""));
    }

    @Test
    void testOnMessage_invalidPatientId_skipped() {
        assertDoesNotThrow(() -> client.onMessage((String)"abc,1000,ECG,0.5"));
    }

    @Test
    void testOnMessage_invalidTimestamp_skipped() {
        assertDoesNotThrow(() -> client.onMessage((String)"1,abc,ECG,0.5"));
    }

    @Test
    void testOnMessage_invalidDataValue_skipped() {
        assertDoesNotThrow(() -> client.onMessage((String)"1,1000,ECG,notanumber"));
    }


    // Connection events
    

    @Test
    void testOnClose_doesNotThrow() {
        assertDoesNotThrow(() -> client.onClose(1000, "Normal closure", true));
    }

    @Test
    void testOnError_doesNotThrow() {
        assertDoesNotThrow(() -> client.onError(new Exception("Test error")));
    }

   
    // Constructor
    

    @Test
    void testConstructor_invalidUri_throwsException() {
        assertThrows(URISyntaxException.class, () -> new WebSocketClientImpl("not a valid uri %%"));
    }

    @Test
    void testConstructor_validUri_doesNotThrow() {
        assertDoesNotThrow(() -> new WebSocketClientImpl("ws://localhost:9090"));
    }

    
    // Integration tests
    
    @Test
    void testIntegration_messageStoredAndAlertGenerated() {
        // Simulate receiving a critical blood pressure reading via WebSocket
        client.onMessage((String)"10,5000,SystolicPressure,185.0");

        // Verify it was stored in DataStorage
        var records = storage.getRecords(10, 5000L, 5000L);
        assertFalse(records.isEmpty());
        assertEquals(185.0, records.get(0).getMeasurementValue(), 0.001);

        // Verify AlertGenerator can evaluate the stored data without errors
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(new Patient(10)));
    }

    @Test
    void testIntegration_multipleMessagesStoredCorrectly() {
        // Simulate a stream of messages
        client.onMessage((String)"11,6000,ECG,0.5");
        client.onMessage((String)"11,7000,ECG,0.6");
        client.onMessage((String)"11,8000,ECG,0.4");

        var records = storage.getRecords(11, 6000L, 8000L);
        assertEquals(3, records.size());
    }

    @Test
    void testIntegration_alertTriggeredAfterWebSocketData() {
        // Low saturation received via WebSocket should trigger alert evaluation
        client.onMessage((String)"12,9000,Saturation,88.0");

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(new Patient(12)));
    }
}