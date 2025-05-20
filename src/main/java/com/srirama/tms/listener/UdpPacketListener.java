package com.srirama.tms.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.srirama.tms.DataConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(DataConstants.UDP_DATA_LISTENER)
public class UdpPacketListener {

    @Value("${udp.listener.port:9002}")
    private int port;

    private DatagramSocket socket;
    private Thread listenerThread;
    private volatile boolean running = false;

    private final BlockingQueue<String> packetQueue = new LinkedBlockingQueue<>();

    public BlockingQueue<String> getPacketQueue() {
        return packetQueue;
    }

    public synchronized void start() {
        if (running) {
            log.warn("UDP listener already running on port {}", port);
            return;
        }

        try {
            socket = new DatagramSocket(port);
            running = true;
            listenerThread = new Thread(this::listen, "UdpPacketListener-Thread");
            listenerThread.start();
            log.info("UDP listener started on port {}", port);
        } catch (IOException e) {
            log.error("Failed to start UDP listener on port {}", port, e);
            throw new RuntimeException("Unable to start UDP listener", e);
        }
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        log.info("UDP listener stopped.");
    }

    public void shutdownOnExit() {
        stop();
    }

    private void listen() {
        byte[] buffer = new byte[1024];

        try {
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Blocking

                String message = new String(packet.getData(), 0, packet.getLength());
                packetQueue.put(message);
                log.debug("Received UDP packet: {}", message);
            }
        } catch (IOException | InterruptedException e) {
            if (running) {
                log.error("Error while receiving UDP packet", e);
            } else {
                log.info("UDP listener socket closed.");
            }
        }
    }
}
