package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This defines the contract for the patient data generators
 * Any class that generates patient health data should implement this interface
 */
public interface PatientDataGenerator {

    /**
     * This generates and outputs health data for a specific patient
     * @param patientId the ID of the patient for who to generate data for
     * @param outputStrategy the strategy used to output the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
