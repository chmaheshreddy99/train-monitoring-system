package com.srirama.tms.connector;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
@Service
public class UDPConnector {

	@Value("${upd.host:localhost}")
    private String host;
	
	@Value("${upd.port:9002}")
    private int port;
	
	@Value("${upd.timeout:5000}")
    private int timeoutMillis;

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

            byte[] buffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            socket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (SocketTimeoutException e) {
            throw new Exception("Timeout: No response received within " + timeoutMillis + "ms", e);
        }
    }
}
