package com.srirama.db.orm;

import com.srirama.tms.entity.Configurations;

public class EntityManagerTest {

    public static void main(String[] args) {

        // Initialize the EntityManager
        EntityManager entityManager = new EntityManager();

        // Create a new Configurations entity
        Configurations newConfig = new Configurations(
            "MetricA",
            12.34,
            220.5,
            1500.75,
            "Test configuration for Metric A"
        );

        // Save the configuration to the "configurations" table
        entityManager.save(newConfig);

        // Fetch all configurations
        System.out.println("Fetching all configurations:");
        for (Configurations config : entityManager.findAll(Configurations.class)) {
            System.out.println(config);
        }

        // Find a specific configuration by metricName (Primary Key)
        Configurations foundConfig = entityManager.findById(Configurations.class, "MetricA");
        System.out.println("Found configuration with metricName 'MetricA': " + foundConfig);

        // Update a configuration
        foundConfig.setSpeed(1800.0);  // Change speed
        entityManager.save(foundConfig);  // save will update the existing record

        // Fetch all configurations after the update
        System.out.println("Fetching all configurations after update:");
        for (Configurations config : entityManager.findAll(Configurations.class)) {
            System.out.println(config);
        }

        // Delete a configuration
       // entityManager.delete(Configurations.class, "MetricA");

        // Fetch all configurations after deletion
        System.out.println("Fetching all configurations after deletion:");
        
       System.out.println(entityManager.findById(Configurations.class, "MetricA"));
       
    }
}
