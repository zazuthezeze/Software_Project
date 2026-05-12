package data_management;

import com.alerts.*;
import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DesignPatternsTest {

    // Factory Method
    @Test
    void testBloodPressureAlertFactory_createsAlert() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "High Systolic", 1000L);
        assertEquals("1", alert.getPatientId());
        assertEquals("High Systolic", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }

    @Test
    void testBloodOxygenAlertFactory_createsAlert() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("2", "Low Saturation", 2000L);
        assertEquals("2", alert.getPatientId());
        assertEquals("Low Saturation", alert.getCondition());
    }

    @Test
    void testECGAlertFactory_createsAlert() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("3", "Abnormal ECG", 3000L);
        assertEquals("3", alert.getPatientId());
        assertEquals("Abnormal ECG", alert.getCondition());
    }

    // Decorator
    @Test
    void testRepeatedAlertDecorator_addsRepeatInfo() {
        Alert base = new Alert("1", "High BP", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 3);
        assertTrue(repeated.getCondition().contains("Repeated"));
        assertEquals(3, repeated.getRepeatCount());
    }

    @Test
    void testPriorityAlertDecorator_addsPriorityTag() {
        Alert base = new Alert("1", "Low Oxygen", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "HIGH");
        assertTrue(priority.getCondition().contains("HIGH"));
        assertEquals("HIGH", priority.getPriority());
    }

    @Test
    void testDecoratorChaining_bothDecoratorsApplied() {
        Alert base = new Alert("1", "ECG Anomaly", 1000L);
        Alert repeated = new RepeatedAlertDecorator(base, 2);
        Alert priority = new PriorityAlertDecorator(repeated, "CRITICAL");
        assertTrue(priority.getCondition().contains("CRITICAL"));
    }

    @Test
    void testDecorator_preservesPatientIdAndTimestamp() {
        Alert base = new Alert("5", "Test", 9999L);
        PriorityAlertDecorator decorated = new PriorityAlertDecorator(base, "LOW");
        assertEquals("5", decorated.getPatientId());
        assertEquals(9999L, decorated.getTimestamp());
    }

    // Singleton
    @Test
    void testDataStorage_singletonReturnsSameInstance() {
        DataStorage a = DataStorage.getInstance();
        DataStorage b = DataStorage.getInstance();
        assertSame(a, b);
    }

    @Test
    void testDataStorage_singletonDataPersists() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(998, 100.0, "TestLabel", 1000L);
        DataStorage same = DataStorage.getInstance();
        assertFalse(same.getRecords(998, 1000L, 1000L).isEmpty());
    }
}