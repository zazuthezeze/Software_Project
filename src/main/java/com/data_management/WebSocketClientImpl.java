package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

/**
 * Implements DataReader by connecting to a WebSocket server and receiving real time patient data
 * Messages are parsed and stored in DataStorage.
 *
 * Message format from the server: patientId,timestamp,label,data
 */
public class WebSocketClientImpl extends WebSocketClient implements DataReader {

    private DataStorage dataStorage;

    /**
     * Creates a WebSocket client that will connect to the given server URI.
     *
     * @param serverUri the URI of the WebSocket server (e.g. ws://localhost:8080)
     * @throws URISyntaxException if the URI is invalid
     */
    public WebSocketClientImpl(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }

    /**
     * Connects to the WebSocket server and starts receiving data.
     * Blocks until the connection is closed.
     *
     * @param dataStorage the storage where received data will be stored
     * @throws IOException if the connection cannot be established
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            // Connect and wait for messages
            this.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("WebSocket connection was interrupted", e);
        }
    }

    /**
     * Called when the connection to the server is established.
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server");
    }

    /**
     * Called when a message is received from the server.
     * Parses the message and stores the data in DataStorage.
     *
     * Message format: patientId,timestamp,label,data
     */
    @Override
    public void onMessage(String message) {
        try {
            String[] parts = message.split(",");
            if (parts.length != 4) {
                System.err.println("Skipping malformed message: " + message);
                return;
            }

            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String label = parts[2].trim();
            String dataStr = parts[3].trim();

            // Alert files use "triggered"/"resolved" instead of a number
            double value;
            if (dataStr.equalsIgnoreCase("triggered")) value = 1.0;
            else if (dataStr.equalsIgnoreCase("resolved")) value = 0.0;
            else value = Double.parseDouble(dataStr);

            dataStorage.addPatientData(patientId, value, label, timestamp);

        } catch (Exception e) {
            System.err.println("Error parsing message: " + message + " - " + e.getMessage());
        }
    }

    /**
     * Called when the connection is closed.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server. Reason: " + reason);
    }

    /**
     * Called when an error occurs. Logs the error but keeps running.
     */
    @Override
    public void onError(Exception e) {
        System.err.println("WebSocket error: " + e.getMessage());
    }
    /**
 * Stops the WebSocket connection.
 */
@Override
public void stopStreaming() {
    try {
        this.closeBlocking();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.err.println("Error closing WebSocket connection: " + e.getMessage());
        }
    }
}