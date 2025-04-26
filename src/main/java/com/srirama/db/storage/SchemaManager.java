package com.srirama.db.storage;

import com.srirama.db.entity.TableSchema;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class SchemaManager {
    private static final Path METADATA_PATH = Paths.get("db_metadata", "tables.meta");
    private final Map<String, TableSchema> schemas = new HashMap<>();

    public SchemaManager() {
        loadSchemas();
    }

    private void loadSchemas() {
        try {
            if (!Files.exists(METADATA_PATH)) {
                Files.createDirectories(METADATA_PATH.getParent());
                Files.createFile(METADATA_PATH);
            }

            try (BufferedReader reader = Files.newBufferedReader(METADATA_PATH)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    TableSchema schema = new TableSchema(parts[0]);
                    for (int i = 1; i < parts.length; i += 2) {
                        schema.addColumn(parts[i], parts[i + 1]);
                    }
                    schemas.put(schema.getTableName(), schema);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schemas", e);
        }
    }

    public void saveSchemas() {
        try (BufferedWriter writer = Files.newBufferedWriter(METADATA_PATH)) {
            for (TableSchema schema : schemas.values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(schema.getTableName());
                for (Map.Entry<String, String> entry : schema.getColumns().entrySet()) {
                    sb.append("|").append(entry.getKey()).append("|").append(entry.getValue());
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save schemas", e);
        }
    }

    public void registerSchema(TableSchema schema) {
        schemas.put(schema.getTableName(), schema);
        saveSchemas();
    }

    public TableSchema getSchema(String tableName) {
        return schemas.get(tableName);
    }

    public Map<String, TableSchema> getAllSchemas() {
        return schemas;
    }
}
