package com.srirama.tms.entity;

import com.srirama.db.annotations.Column;
import com.srirama.db.annotations.Id;
import com.srirama.db.annotations.Table;

@Table(name = "configurations")
public class Configurations {

    @Id
    @Column(name = "metricName", type = "String")
    private String metricName;

    @Column(name = "load", type = "Double")
    private Double load;

    @Column(name = "voltage", type = "Double")
    private Double voltage;

    @Column(name = "speed", type = "Double")
    private Double speed;

    @Column(name = "metricDescription", type = "String")
    private String metricDescription;

    // Constructors
    public Configurations(String metricName, Double load, Double voltage, Double speed, String metricDescription) {
        this.metricName = metricName;
        this.load = load;
        this.voltage = voltage;
        this.speed = speed;
        this.metricDescription = metricDescription;
    }

    public Configurations() {
        // Default constructor for ORM
    }

    // Getters and Setters
    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Double getLoad() {
        return load;
    }

    public void setLoad(Double load) {
        this.load = load;
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getMetricDescription() {
        return metricDescription;
    }

    public void setMetricDescription(String metricDescription) {
        this.metricDescription = metricDescription;
    }

    @Override
    public String toString() {
        return "Configurations{" +
                "metricName='" + metricName + '\'' +
                ", load=" + load +
                ", voltage=" + voltage +
                ", speed=" + speed +
                ", metricDescription='" + metricDescription + '\'' +
                '}';
    }
}
