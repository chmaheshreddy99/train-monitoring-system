package com.srirama.db.storage;

import com.srirama.db.entity.TableRecord;
import com.srirama.db.entity.TableSchema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RecordStorage {
    private static final Path STORAGE_DIR = Paths.get("db_tables");

    public RecordStorage() {
        try {
            if (!Files.exists(STORAGE_DIR)) {
                Files.createDirectories(STORAGE_DIR);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage folder", e);
        }
    }

    public void insertRecord(String tableName, TableRecord record) {
        Path filePath = STORAGE_DIR.resolve(tableName + ".tbl");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
            String line = String.join("|", record.getFields().values());
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to insert record into " + tableName, e);
        }
    }

    public void readRecords(String tableName, Consumer<TableRecord> recordConsumer, TableSchema schema) {
        Path filePath = STORAGE_DIR.resolve(tableName + ".tbl");
        if (!Files.exists(filePath)) {
            System.out.println("No data found for table: " + tableName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                TableRecord record = parseLineToRecord(line, schema);
                recordConsumer.accept(record);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read records from " + tableName, e);
        }
    }

    public TableRecord findRecordById(String tableName, String idColumn, String idValue, TableSchema schema) {
        Path filePath = STORAGE_DIR.resolve(tableName + ".tbl");
        if (!Files.exists(filePath)) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                TableRecord record = parseLineToRecord(line, schema);
                if (idValue.equals(record.getField(idColumn))) {
                    return record;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to find record by ID in " + tableName, e);
        }
        return null;
    }

    public boolean updateRecord(String tableName, String idColumn, String idValue, TableRecord updatedRecord, TableSchema schema) {
        Path filePath = STORAGE_DIR.resolve(tableName + ".tbl");
        if (!Files.exists(filePath)) {
            throw new RuntimeException("No table found: " + tableName);
        }

        List<String> allLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                TableRecord record = parseLineToRecord(line, schema);
                if (idValue.equals(record.getField(idColumn))) {
                    allLines.add(String.join("|", updatedRecord.getFields().values()));
                } else {
                    allLines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read records during update for " + tableName, e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false))) {
            for (String l : allLines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write updated records for " + tableName, e);
        }
        return true;
    }

    public boolean deleteRecord(String tableName, String idColumn, String idValue, TableSchema schema) {
        Path filePath = STORAGE_DIR.resolve(tableName + ".tbl");
        if (!Files.exists(filePath)) {
            throw new RuntimeException("No table found: " + tableName);
        }

        List<String> remainingLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                TableRecord record = parseLineToRecord(line, schema);
                if (!idValue.equals(record.getField(idColumn))) {
                    remainingLines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read records during delete for " + tableName, e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false))) {
            for (String l : remainingLines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write records after delete for " + tableName, e);
        }
        return true;
    }

    private TableRecord parseLineToRecord(String line, TableSchema schema) {
        String[] values = line.split("\\|");
        TableRecord record = new TableRecord();
        int i = 0;
        for (String column : schema.getColumns().keySet()) {
            if (i < values.length) {
                record.setField(column, values[i]);
            }
            i++;
        }
        return record;
    }
}
