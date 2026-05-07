package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This generates simulated blood saturation data for patients
 * it keeps track of each patient's last saturation value 
 * and applies small random fluctuations to simulate realistic readings
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    /**
     * Random number generator for simulating saturation fluctuations
     */
    private static final Random random = new Random();
    /**
     * Stores the last blood saturation value for each patient
     */
    private int[] lastSaturationValues;

    /**
     * This creates a new BloodSaturationDataGenerator and gives each patient
     * a baseline saturation value between 95 and 100
     * @param patientCount the number of patients to generate data for
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * This generates and outputs a blood saturation reading for a given patient
     * it applies a small random variation to the last recorded value and keeps
     * the result within a realistic range of 90 to 100 percent.
     * @param patientId the ID of the patient to generate data for
     * @param outputStrategy the strategy used to output the generated data
     * @throws Exception if an error occurs during data generation
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
