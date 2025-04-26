package com.srirama.tms.metrics;

import java.util.List;

public class MetricGroup {

    private String groupName;
    private List<Metric> metrics;

    public MetricGroup(String groupName, List<Metric> metrics) {
        this.groupName = groupName;
        this.metrics = metrics;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }
}
