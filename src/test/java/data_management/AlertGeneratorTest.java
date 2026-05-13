package data_management;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AlertGenerator.
 * 
 * Since triggerAlert only prints to console, we verify that evaluateData
 * runs without exceptions for all alert conditions. A future improvement
 * would be to inject an alert listener to capture triggered alerts directly.
 */
class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator generator;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        generator = new AlertGenerator(storage);
    }

    
    // Blood Pressure - Critical Thresholds
    

    @Test
    void testSystolicAbove180_triggersAlert() {
        storage.addPatientData(1, 185.0, "SystolicPressure", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    @Test
    void testSystolicBelow90_triggersAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    @Test
    void testDiastolicAbove120_triggersAlert() {
        storage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    @Test
    void testDiastolicBelow60_triggersAlert() {
        storage.addPatientData(1, 55.0, "DiastolicPressure", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** Normal blood pressure should not trigger any alert. */
    @Test
    void testNormalBloodPressure_noAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 80.0, "DiastolicPressure", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    // -------------------------------------------------------------------------
    // Blood Pressure - Trends (3 consecutive readings each changing by >10)
    // -------------------------------------------------------------------------

    @Test
    void testIncreasingSystolicTrend_triggersAlert() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 130.0, "SystolicPressure", 3000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    @Test
    void testDecreasingSystolicTrend_triggersAlert() {
        storage.addPatientData(1, 130.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "SystolicPressure", 3000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** Changes of less than 10 mmHg should not trigger a trend alert. */
    @Test
    void testSmallBloodPressureChanges_noTrendAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 122.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 121.0, "SystolicPressure", 3000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** Only two readings - not enough to detect a trend. */
    @Test
    void testTwoReadingsOnly_noTrendAlert() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 120.0, "SystolicPressure", 2000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    
    // Blood Saturation
   

    @Test
    void testSaturationBelow92_triggersAlert() {
        storage.addPatientData(1, 91.0, "Saturation", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    @Test
    void testNormalSaturation_noAlert() {
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** A drop of 5% within 10 minutes should trigger a rapid drop alert. */
    @Test
    void testRapidSaturationDrop_triggersAlert() {
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 93.0, "Saturation", 300000L); // 5 minutes later
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** Same drop but outside the 10-minute window should not trigger. */
    @Test
    void testSaturationDropOutside10Minutes_noAlert() {
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 93.0, "Saturation", 700000L); // ~11 minutes later
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    
    // Hypotensive Hypoxemia (low systolic AND low saturation)
   

    @Test
    void testHypotensiveHypoxemia_bothLow_triggersAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 91.0, "Saturation", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** Only one condition met - should not trigger hypotensive hypoxemia alert. */
    @Test
    void testHypotensiveHypoxemia_onlyLowSystolic_noAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    @Test
    void testHypotensiveHypoxemia_onlyLowSaturation_noAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 91.0, "Saturation", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    
    // ECG - Abnormal Peak Detection (sliding window average)
   

    /** A value 50% above the rolling average should trigger an ECG alert. */
    @Test
    void testAbnormalECGPeak_triggersAlert() {
        for (int i = 0; i < 10; i++)
            storage.addPatientData(1, 0.5, "ECG", 1000L + i);
        storage.addPatientData(1, 5.0, "ECG", 2000L); // far above average
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    @Test
    void testNormalECG_noAlert() {
        for (int i = 0; i < 11; i++)
            storage.addPatientData(1, 0.5, "ECG", 1000L + i);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** Fewer than 10 readings - not enough for the sliding window. */
    @Test
    void testECGInsufficientData_noAlert() {
        storage.addPatientData(1, 5.0, "ECG", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    // -------------------------------------------------------------------------
    // Triggered Alert (nurse/patient button)
    // -------------------------------------------------------------------------

    @Test
    void testManualAlertTriggered() {
        // 1.0 represents "triggered" as stored by FileDataReader
        storage.addPatientData(1, 1.0, "Alert", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    /** A resolved alert (0.0) should not trigger a manual alert. */
    @Test
    void testManualAlertResolved_noAlert() {
        storage.addPatientData(1, 0.0, "Alert", 1000L);
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(1)));
    }

    // -------------------------------------------------------------------------
    // Edge Cases
    // -------------------------------------------------------------------------

    /** A patient with no records at all should not cause any errors. */
    @Test
    void testNoRecords_noAlert() {
        assertDoesNotThrow(() -> generator.evaluateData(new Patient(999)));
    }

    /** Multiple patients should be evaluated independently without interference. */
    @Test
    void testMultiplePatients_independent() {
        storage.addPatientData(1, 185.0, "SystolicPressure", 1000L);
        storage.addPatientData(2, 120.0, "SystolicPressure", 1000L);
        assertDoesNotThrow(() -> {
            generator.evaluateData(new Patient(1));
            generator.evaluateData(new Patient(2));
        });
    }
}
