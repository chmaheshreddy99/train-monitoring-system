package com.srirama.db.orm;

import com.srirama.db.entity.TableRecord;
import com.srirama.db.entity.TableSchema;
import com.srirama.db.storage.RecordStorage;
import com.srirama.db.storage.SchemaManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private final SchemaManager schemaManager;
    private final RecordStorage recordStorage;

    public EntityManager() {
        this.schemaManager = new SchemaManager();
        this.recordStorage = new RecordStorage();
    }

    public <T> void save(T entity) {
        try {
            Class<?> clazz = entity.getClass();
            if (!clazz.isAnnotationPresent(com.srirama.db.annotations.Table.class)) {
                throw new IllegalArgumentException("Entity must have @Table annotation");
            }

            com.srirama.db.annotations.Table tableAnnotation = clazz.getAnnotation(com.srirama.db.annotations.Table.class);
            String tableName = tableAnnotation.name();

            if (schemaManager.getSchema(tableName) == null) {
                TableSchema schema = EntityMapper.toSchema(clazz);
                schemaManager.registerSchema(schema);
            }

            TableRecord record = EntityMapper.toRecord(entity);
            recordStorage.insertRecord(tableName, record);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity", e);
        }
    }

    public <T> List<T> findAll(Class<T> clazz) {
        try {
            validateEntity(clazz);

            com.srirama.db.annotations.Table tableAnnotation = clazz.getAnnotation(com.srirama.db.annotations.Table.class);
            String tableName = tableAnnotation.name();
            TableSchema schema = schemaManager.getSchema(tableName);

            List<T> results = new ArrayList<>();
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            recordStorage.readRecords(tableName, record -> {
                try {
                    T entity = constructor.newInstance();
                    mapRecordToEntity(record, entity);
                    results.add(entity);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to map record to entity", e);
                }
            }, schema);

            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch entities", e);
        }
    }

    public <T> T findById(Class<T> clazz, String idValue) {
        try {
            validateEntity(clazz);

            com.srirama.db.annotations.Table tableAnnotation = clazz.getAnnotation(com.srirama.db.annotations.Table.class);
            String tableName = tableAnnotation.name();
            TableSchema schema = schemaManager.getSchema(tableName);

            String idColumn = EntityMapper.getIdColumnName(clazz);

            TableRecord record = recordStorage.findRecordById(tableName, idColumn, idValue, schema);
            if (record == null) {
                return null;
            }

            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T entity = constructor.newInstance();
            mapRecordToEntity(record, entity);

            return entity;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find entity by id", e);
        }
    }

    public <T> void update(T entity) {
        try {
            Class<?> clazz = entity.getClass();
            validateEntity(clazz);

            com.srirama.db.annotations.Table tableAnnotation = clazz.getAnnotation(com.srirama.db.annotations.Table.class);
            String tableName = tableAnnotation.name();
            TableSchema schema = schemaManager.getSchema(tableName);

            String idColumn = EntityMapper.getIdColumnName(clazz);
            String idValue = EntityMapper.getIdValue(entity);

            TableRecord updatedRecord = EntityMapper.toRecord(entity);

            boolean success = recordStorage.updateRecord(tableName, idColumn, idValue, updatedRecord, schema);
            if (!success) {
                throw new RuntimeException("Failed to update entity: ID not found");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to update entity", e);
        }
    }

    public <T> boolean delete(Class<T> clazz, String idValue) {
        try {
            validateEntity(clazz);

            com.srirama.db.annotations.Table tableAnnotation = clazz.getAnnotation(com.srirama.db.annotations.Table.class);
            String tableName = tableAnnotation.name();
            TableSchema schema = schemaManager.getSchema(tableName);

            String idColumn = EntityMapper.getIdColumnName(clazz);

            return recordStorage.deleteRecord(tableName, idColumn, idValue, schema);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete entity", e);
        }
    }

    // --- Helper Methods ---

    private <T> void mapRecordToEntity(TableRecord record, T entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(com.srirama.db.annotations.Column.class)) {
                field.setAccessible(true);
                com.srirama.db.annotations.Column columnAnnotation = field.getAnnotation(com.srirama.db.annotations.Column.class);
                String value = record.getField(columnAnnotation.name());
                if (value != null) {
                    Object converted = convert(field.getType(), value);
                    field.set(entity, converted);
                }
            }
        }
    }

    private void validateEntity(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(com.srirama.db.annotations.Table.class)) {
            throw new IllegalArgumentException("Entity must have @Table annotation");
        }
    }

    private Object convert(Class<?> type, String value) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; // default: String
    }
}
