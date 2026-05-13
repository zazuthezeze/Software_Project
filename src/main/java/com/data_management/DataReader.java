package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a real-time data source and continuously receives data.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if the connection cannot be established
     */
    default void startStreaming(DataStorage dataStorage) throws IOException {
        readData(dataStorage);
    }

    /**
     * Stops the real-time data stream.
     */
    default void stopStreaming() {
        // Default implementation does nothing — override for streaming sources
    }
}
