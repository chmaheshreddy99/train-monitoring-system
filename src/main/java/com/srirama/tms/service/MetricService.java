package com.srirama.tms.service;

import java.util.Arrays;
import java.util.List;

import com.srirama.tms.metrics.Metric;
import com.srirama.tms.metrics.MetricGroup;

public class MetricService {

    public List<MetricGroup> getMetricGroups() {
        return Arrays.asList(
            new MetricGroup("Electrical", Arrays.asList(
                new Metric("Voltage"),
                new Metric("Current")
            )),
            new MetricGroup("Mechanical", Arrays.asList(
                new Metric("Torque"),
                new Metric("Acceleration"),
                new Metric("Speed")
            ))
        );
    }
}
