package com.srirama.tms.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UDPListener {

	private static final Logger logger = LoggerFactory.getLogger(UDPListener.class);

	private final int port;
	private final BlockingQueue<String> queue;
	private volatile boolean running = true;
	private DatagramSocket socket;

	public UDPListener(int port, int bufferSize) {
		this.port = port;
		this.queue = new ArrayBlockingQueue<>(bufferSize);
	}

	public void start() {
		logger.info("Starting UDP Listener on port {}", port);
		new Thread(this::listen, "UDP-Listener-Thread").start();
		new Thread(this::consume, "UDP-Consumer-Thread").start();
	}

	private void listen() {
		try {
			socket = new DatagramSocket(port);
			byte[] buffer = new byte[2048]; // generous buffer

			while (running) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				byte[] data = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());

				String hexPayload = bytesToHex(data);

				if (verifyChecksum(hexPayload)) {
					queue.put(hexPayload);
				} else {
					logger.warn("Invalid checksum, discarded packet: {}", hexPayload);
				}
			}
		} catch (SocketException e) {
			if (running) {
				logger.error("Socket error while listening", e);
			}
		} catch (Exception e) {
			logger.error("Unexpected error in listener", e);
		}
	}

	private void consume() {
		try {
			while (running) {
				String record = queue.take(); // blocking
				processRecord(record);
			}
		} catch (InterruptedException e) {
			logger.warn("Consumer thread interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private void processRecord(String hexRecord) {
		logger.info("Processing valid record: {}", hexRecord);
		// TODO: Further decoding or business logic
	}

	private boolean verifyChecksum(String hexPayload) {
		if (hexPayload.length() < 4) {
			logger.debug("Packet too short for checksum verification: {}", hexPayload);
			return false;
		}

		try {
			String dataPart = hexPayload.substring(0, hexPayload.length() - 2);
			String checksumPart = hexPayload.substring(hexPayload.length() - 2);

			int computedChecksum = 0;
			for (int i = 0; i < dataPart.length(); i += 2) {
				String byteStr = dataPart.substring(i, i + 2);
				computedChecksum += Integer.parseInt(byteStr, 16);
			}

			computedChecksum = computedChecksum & 0xFF; // Only last 8 bits

			int receivedChecksum = Integer.parseInt(checksumPart, 16);

			boolean valid = computedChecksum == receivedChecksum;
			if (!valid) {
				logger.debug("Checksum mismatch: computed=0x{}, received=0x{}", Integer.toHexString(computedChecksum),
						checksumPart);
			}
			return valid;
		} catch (Exception e) {
			logger.error("Checksum validation error", e);
			return false;
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	public void stop() {
		logger.info("Stopping UDP Listener...");
		running = false;
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}
	}

	public BlockingQueue<String> getQueue() {
		return queue;
	}
}
