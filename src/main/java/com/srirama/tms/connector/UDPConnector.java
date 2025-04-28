package com.srirama.tms.connector;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * The {@code UDPConnector} class provides a simple interface for sending and receiving
 * messages over UDP (User Datagram Protocol).
 * <p>
 * This class facilitates communication with a specified host and port using UDP,
 * allowing for sending a message and receiving a response within a specified timeout period.
 * </p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * UDPConnector connector = new UDPConnector("127.0.0.1", 9000);
 * String response = connector.sendAndReceive("Hello Server!");
 * System.out.println("Received: " + response);
 * }</pre>
 *
 * @author
 */
public class UDPConnector {

    private final String host;
    private final int port;
    private final int timeoutMillis;

    /**
     * Constructs a {@code UDPConnector} with the specified host and port.
     * Uses a default timeout of 5000 milliseconds.
     *
     * @param host the target host to connect to
     * @param port the target port to connect to
     */
    public UDPConnector(String host, int port) {
        this(host, port, 5000);
    }

    /**
     * Constructs a {@code UDPConnector} with the specified host, port, and timeout.
     *
     * @param host         the target host to connect to
     * @param port         the target port to connect to
     * @param timeoutMillis the timeout in milliseconds for receiving a response
     */
    public UDPConnector(String host, int port, int timeoutMillis) {
        this.host = host;
        this.port = port;
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * Sends a message to the specified host and port over UDP and waits for a response.
     *
     * @param message the message to send
     * @return the response received from the server
     * @throws Exception if an I/O error occurs or if the operation times out
     */
    public String sendAndReceive(String message) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeoutMillis);

            InetAddress address = InetAddress.getByName(host);
            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            byte[] buffer = new byte[4096]; // 4KB buffer for receiving
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            socket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (SocketTimeoutException e) {
            throw new Exception("Timeout: No response received within " + timeoutMillis + "ms", e);
        }
    }
}
