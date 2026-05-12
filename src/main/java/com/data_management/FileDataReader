package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileDataReader implements DataReader {

    private final String outputDirectory;

    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try (Stream<Path> files = Files.list(Paths.get(outputDirectory))) {
            files.filter(p -> p.toString().endsWith(".txt"))
                 .forEach(file -> {
                     try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                         String line;
                         while ((line = reader.readLine()) != null) {
                             if (line.trim().isEmpty()) continue;
                             // Format: Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.5
                             String[] parts = line.split(", ");
                             int patientId = Integer.parseInt(parts[0].split(": ")[1]);
                             long timestamp = Long.parseLong(parts[1].split(": ")[1]);
                             String label = parts[2].split(": ")[1];
                             String dataStr = parts[3].split(": ")[1];
                             double value = dataStr.equals("triggered") ? 1.0
                                          : dataStr.equals("resolved")  ? 0.0
                                          : Double.parseDouble(dataStr);
                             dataStorage.addPatientData(patientId, value, label, timestamp);
                         }
                     } catch (Exception e) {
                         System.err.println("Error reading " + file + ": " + e.getMessage());
                     }
                 });
        }
    }
}
