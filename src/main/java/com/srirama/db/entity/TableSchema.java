package com.srirama.db.entity;

import java.util.LinkedHashMap;

public class TableSchema {
    private final String tableName;
    private final LinkedHashMap<String, String> columns = new LinkedHashMap<>();

    public TableSchema(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(String columnName, String dataType) {
        columns.put(columnName, dataType);
    }

    public String getTableName() {
        return tableName;
    }

    public LinkedHashMap<String, String> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return "TableSchema{" +
                "tableName='" + tableName + '\'' +
                ", columns=" + columns +
                '}';
    }
}
