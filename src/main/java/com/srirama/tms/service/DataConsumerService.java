package com.srirama.tms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.srirama.tms.listener.DataListener;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataConsumerService {

    private static final int DATA_SIZE = 7;
    
    @Autowired
    @Qualifier("fileDataListener")
    private DataListener<String> dataListener;

    private final String[] data = new String[DATA_SIZE];

    public synchronized String[] fetchData() {
        try {
            for (int i = 0; i < DATA_SIZE; i++) {
                data[i] = dataListener.getQueue().take();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while fetching UDP packets", e);
        }

        return data;
    }
}
