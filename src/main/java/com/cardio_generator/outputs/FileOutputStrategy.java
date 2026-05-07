package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This implements the OutputStrategy interface to write patient data into the files
 * Each data label gets its own file stored in the specified base directory
 */
// changed the fileOutputStrategy name to FileOutputStrategy to follow UpperCamelCase
public class FileOutputStrategy implements OutputStrategy {

    /**
     * The baseDirectory is the directory where the output files will be stored
     */
    // Changed name from BaseDirectory to baseDirectory to follow lowerCamelCase
    private String baseDirectory;

    /**
     * This maps each data label to it's corresponding file path
     */
    // Changed field name from file_map to fileMap to follow lowerCamelCase
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * This creates a new FileOutputStrategy with the specified base directory
     * @param baseDirectory the directory where the output files will be created
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }
    /**
     * This writes the generated patient data to a file which corresponds to a given label
     * In case the file does not exist it will be created, otherwise data is appended
     * @param patientId the ID of the patient the data belongs to
     * @param timestamp the time at which the data was generated
     * @param label the type of data being output, used as the filename
     * @param data the generated data value as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // Changed variable name from FilePath to filePath to follow lowerCamelCase
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}