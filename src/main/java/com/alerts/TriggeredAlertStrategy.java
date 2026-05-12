package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;

/** Checks for manually triggered alerts from nurses or patients. */
public class TriggeredAlertStrategy implements AlertStrategy {

    @Override
    public void check(int patientId, List<PatientRecord> records) {
        for (PatientRecord r : records) {
            if (r.getRecordType().equals("Alert") && r.getMeasurementValue() == 1.0)
                triggerAlert(new Alert(String.valueOf(patientId), "Manual Alert Triggered", r.getTimestamp()));
        }
    }

    private void triggerAlert(Alert alert) {
        System.out.println("ALERT - Patient " + alert.getPatientId() + " | " + alert.getCondition() + " | " + alert.getTimestamp());
    }
}
