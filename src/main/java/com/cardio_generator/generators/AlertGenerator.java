package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This will generate a simulated alert about data for patients
 * the models alert triggering and resolution using probability
 * where each patient can have an active or resolved alert state
 */
public class AlertGenerator implements PatientDataGenerator {

    /**
     * A random number generator is used to determine alert triggering and resolution
     */
    public static final Random randomGenerator = new Random();
      /**
     * Stores the current alert state for each patient
     * False means resolved while true means triggered
     */
    private boolean[] alertstates; // false = resolved, true = pressed

    /**
     * this creates a new AlertGenerator and initializes alert states for all patients
     * @param patientCount the number of patients to generate alerts for
     */
    public AlertGenerator(int patientCount) {
        // Changed indentation to 2 spaces
        alertstates = new boolean[patientCount + 1];
    }
    /**
     * this will generate and output an alert status for the given patient
     * If an alert is active there is a 90% chance it gets resolved
     * If no alert is active a new one may be triggered based on a probability calculation 
     * @param patientId the ID of the patient to generate an alert for
     * @param outputStrategy the strategy used to output the generated alert
     * @throws Exception if an error occurs during alert generation
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertstates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertstates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double Lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-Lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertstates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
