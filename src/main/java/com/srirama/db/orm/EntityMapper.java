package com.srirama.db.orm;

import com.srirama.db.annotations.Column;
import com.srirama.db.annotations.Id;
import com.srirama.db.annotations.Table;
import com.srirama.db.entity.TableRecord;
import com.srirama.db.entity.TableSchema;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public static <T> TableSchema toSchema(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class must be annotated with @Table");
        }
        Table table = clazz.getAnnotation(Table.class);
        TableSchema schema = new TableSchema(table.name());

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                schema.addColumn(column.name(), column.type());
            }
            else {
            	schema.addColumn(field.getName(), field.getType().getName());
            }
        }
        return schema;
    }

    public static <T> TableRecord toRecord(T entity) {
        try {
            Class<?> clazz = entity.getClass();
            TableRecord record = new TableRecord();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
            	Object value = field.get(entity);
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    record.setField(column.name(), value != null ? value.toString() : "");
                }
                else {
                	record.setField(field.getName(), value != null ? value.toString() : "");
                }
            }
            return record;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to map entity to record", e);
        }
    }

    /**
     * Find which column is the primary key (@Id).
     */
    public static <T> String getIdColumnName(Class<T> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                return column.name();
            }
        }
        throw new IllegalStateException("No field annotated with @Id found in class " + clazz.getName());
    }

    /**
     * Extract the primary key value from an entity.
     */
    public static <T> String getIdValue(T entity) {
        try {
            Class<?> clazz = entity.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value != null) {
                        return value.toString();
                    }
                }
            }
            throw new IllegalStateException("No @Id field found or value is null in entity " + clazz.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get ID value from entity", e);
        }
    }
}
