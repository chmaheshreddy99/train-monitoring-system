package com.srirama.tms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.srirama.tms.listener.UdpPacketListener;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataConsumerService {

    private static final int DATA_SIZE = 7;

    @Autowired
    private UdpPacketListener udpListener;

    private final String[] data = new String[DATA_SIZE];

    public synchronized String[] fetchData() {
        try {
            for (int i = 0; i < DATA_SIZE; i++) {
                data[i] = udpListener.getPacketQueue().take();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while fetching UDP packets", e);
        }

        return data;
    }
}
