package com.alerts;

/**
 * Base factory class for creating Alert objects.
 * Subclasses override createAlert to return specific alert types,
 * following the Factory Method pattern.
 */
public abstract class AlertFactory {
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}