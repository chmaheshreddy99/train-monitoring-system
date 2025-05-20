package com.srirama.tms.listener;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Utility class to read from a USB Serial Port and push data into a buffer.
 */
public class USBSerialReader implements DataListener<String>{
	
    private final SerialPort serialPort;
    private final BlockingQueue<String> buffer;
    private volatile boolean running = false;

    /**
     * Creates a new USBSerialReader for a specified port and baud rate.
     *
     * @param portDescriptor the serial port identifier (e.g., "COM3" or "/dev/ttyUSB0")
     * @param baudRate       the baud rate for communication (e.g., 9600)
     */
    public USBSerialReader(String portDescriptor, int baudRate) {
        serialPort = SerialPort.getCommPort(portDescriptor);
        serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
        buffer = new LinkedBlockingQueue<>();
    }

    /**
     * Starts reading from the serial port and populates the buffer.
     *
     * @return the buffer holding received lines
     */
    public void start() {
        if (!serialPort.openPort()) {
            throw new RuntimeException("Failed to open port: " + serialPort.getSystemPortName());
        }

        running = true;
        InputStream in = serialPort.getInputStream();
        Scanner scanner = new Scanner(in);

        Thread readThread = new Thread(() -> {
            try {
                while (running && serialPort.isOpen()) {
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        try {
							buffer.put(line);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
                }
            } finally {
                scanner.close();
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    /**
     * Stops reading and closes the serial port.
     */
    public void stop() {
        running = false;
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    /**
     * Lists all available serial ports.
     *
     * @return array of available SerialPort objects
     */
    public static SerialPort[] listAvailablePorts() {
        return SerialPort.getCommPorts();
    }

	@Override
	public BlockingQueue<String> getQueue() {
		return buffer;
	}   
}

