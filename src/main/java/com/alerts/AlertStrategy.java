package com.alerts;

import com.data_management.PatientRecord;
import java.util.List;

/**
 * Interface for alert checking strategies.
 * Each implementation handles one specific type of alert,
 * following the Single Responsibility and Open/Closed principles.
 * New alert types can be added without modifying existing code.
 */
public interface AlertStrategy {
    void checkAlert(int patientId, List<PatientRecord> records);
}