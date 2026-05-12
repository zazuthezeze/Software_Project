package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;

/** Checks for the combined hypotensive hypoxemia condition. */
public class HypotensiveHypoxemiaStrategy implements AlertStrategy {

    @Override
    public void check(int patientId, List<PatientRecord> records) {
        boolean lowSystolic = records.stream()
                .filter(r -> r.getRecordType().equals("SystolicPressure"))
                .anyMatch(r -> r.getMeasurementValue() < 90);
        boolean lowSaturation = records.stream()
                .filter(r -> r.getRecordType().equals("Saturation"))
                .anyMatch(r -> r.getMeasurementValue() < 92);

        if (lowSystolic && lowSaturation)
            triggerAlert(new Alert(String.valueOf(patientId), "Hypotensive Hypoxemia", System.currentTimeMillis()));
    }

    private void triggerAlert(Alert alert) {
        System.out.println("ALERT - Patient " + alert.getPatientId() + " | " + alert.getCondition() + " | " + alert.getTimestamp());
    }
}