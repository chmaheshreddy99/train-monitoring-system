package com.srirama.db.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class TableRecord {

    private final LinkedHashMap<String, String> fields = new LinkedHashMap<>();

    // Set a value for a specific field
    public void setField(String column, String value) {
        fields.put(column, value);
    }

    // Get the value of a specific field
    public String getField(String column) {
        return fields.get(column);
    }

    // Get all fields
    public LinkedHashMap<String, String> getFields() {
        return fields;
    }

    // Check if the record has a field
    public boolean hasField(String column) {
        return fields.containsKey(column);
    }

    // Override equals and hashCode to allow for comparison in updates/deletes
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TableRecord that = (TableRecord) obj;
        return this.fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TableRecord{");
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
        }
        if (sb.length() > 13) { // Remove trailing comma and space
            sb.setLength(sb.length() - 2);
        }
        sb.append('}');
        return sb.toString();
    }
}
