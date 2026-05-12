package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;
import java.util.stream.Collectors;

/** Checks for low saturation and rapid saturation drops. */
public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public void checkAlert(int patientId, List<PatientRecord> records) {
        List<PatientRecord> satRecords = records.stream()
                .filter(r -> r.getRecordType().equals("Saturation"))
                .collect(Collectors.toList());

        for (int i = 0; i < satRecords.size(); i++) {
            PatientRecord r = satRecords.get(i);
            if (r.getMeasurementValue() < 92)
                triggerAlert(new Alert(String.valueOf(patientId), "Low Blood Saturation", r.getTimestamp()));

            for (int j = i + 1; j < satRecords.size(); j++) {
                PatientRecord later = satRecords.get(j);
                if (later.getTimestamp() - r.getTimestamp() > 600000) break;
                if (r.getMeasurementValue() - later.getMeasurementValue() >= 5)
                    triggerAlert(new Alert(String.valueOf(patientId), "Rapid Blood Saturation Drop", later.getTimestamp()));
            }
        }
    }

    private void triggerAlert(Alert alert) {
        System.out.println("ALERT - Patient " + alert.getPatientId() + " | " + alert.getCondition() + " | " + alert.getTimestamp());
    }
}