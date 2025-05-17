package com.srirama.db.orm;

import com.srirama.tms.entity.MetricParameterPreferences;

public class EntityManagerTest {

    public static void main(String[] args) {

        // Initialize the EntityManager
        EntityManager entityManager = new EntityManager();

        MetricParameterPreferences metricParameter =  new MetricParameterPreferences();
        metricParameter.setName("test");
        metricParameter.setDescription("test");
        metricParameter.setMetricParameterName("Voltage");
        // Save the configuration to the "configurations" table
        entityManager.save(metricParameter);

        // Fetch all configurations
        System.out.println("Fetching all configurations:");
        for (MetricParameterPreferences config : entityManager.findAll(MetricParameterPreferences.class)) {
            System.out.println(config);
        }
       
    }
}
