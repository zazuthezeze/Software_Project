package com.cardio_generator.outputs;

/**
 * This defines the contract for the output strategies that are used to send generated patient data
 * Any class that handles data output should implement this interface.
 */
public interface OutputStrategy {
    /**
     * This outputs the generated data for a specific patient
     * @param patientId the ID of the patient the data belongs to
     * @param timestamp the time at which the data was generated
     * @param label the type of data being output, for example ECG or BloodPressure
     * @param data the generated data value as a string
     */
    void output(int patientId, long timestamp, String label, String data);
}
