package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;
import java.util.stream.Collectors;

/** Checks for abnormal ECG peaks using a sliding window average. */
public class ECGStrategy implements AlertStrategy {

    private static final int WINDOW_SIZE = 10;

    @Override
    public void check(int patientId, List<PatientRecord> records) {
        List<PatientRecord> ecg = records.stream()
                .filter(r -> r.getRecordType().equals("ECG"))
                .collect(Collectors.toList());

        for (int i = WINDOW_SIZE; i < ecg.size(); i++) {
            double avg = 0;
            for (int j = i - WINDOW_SIZE; j < i; j++)
                avg += ecg.get(j).getMeasurementValue();
            avg /= WINDOW_SIZE;

            if (ecg.get(i).getMeasurementValue() > avg * 1.5)
                triggerAlert(new Alert(String.valueOf(patientId), "Abnormal ECG Peak", ecg.get(i).getTimestamp()));
        }
    }

    private void triggerAlert(Alert alert) {
        System.out.println("ALERT - Patient " + alert.getPatientId() + " | " + alert.getCondition() + " | " + alert.getTimestamp());
    }
}