package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;
import java.util.stream.Collectors;

/** Checks for critical blood pressure thresholds and trends. */
public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public void check(int patientId, List<PatientRecord> records) {
        List<PatientRecord> systolic = filter(records, "SystolicPressure");
        List<PatientRecord> diastolic = filter(records, "DiastolicPressure");

        for (PatientRecord r : systolic) {
            if (r.getMeasurementValue() > 180 || r.getMeasurementValue() < 90)
                triggerAlert(new Alert(String.valueOf(patientId), "Critical Systolic Pressure", r.getTimestamp()));
        }
        for (PatientRecord r : diastolic) {
            if (r.getMeasurementValue() > 120 || r.getMeasurementValue() < 60)
                triggerAlert(new Alert(String.valueOf(patientId), "Critical Diastolic Pressure", r.getTimestamp()));
        }

        checkTrend(patientId, systolic, "Systolic Pressure Trend");
        checkTrend(patientId, diastolic, "Diastolic Pressure Trend");
    }

    private void checkTrend(int patientId, List<PatientRecord> records, String condition) {
        for (int i = 2; i < records.size(); i++) {
            double a = records.get(i - 2).getMeasurementValue();
            double b = records.get(i - 1).getMeasurementValue();
            double c = records.get(i).getMeasurementValue();
            if ((b - a) > 10 && (c - b) > 10 || (a - b) > 10 && (b - c) > 10)
                triggerAlert(new Alert(String.valueOf(patientId), condition, records.get(i).getTimestamp()));
        }
    }

    private List<PatientRecord> filter(List<PatientRecord> records, String label) {
        return records.stream().filter(r -> r.getRecordType().equals(label)).collect(Collectors.toList());
    }

    private void triggerAlert(Alert alert) {
        System.out.println("ALERT - Patient " + alert.getPatientId() + " | " + alert.getCondition() + " | " + alert.getTimestamp());
    }
}