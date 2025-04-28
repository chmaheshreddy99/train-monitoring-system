package com.srirama.tms.entity;

import com.srirama.db.annotations.Column;
import com.srirama.db.annotations.Id;
import com.srirama.db.annotations.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * MetricParameter - Represents a metric parameter stored in the FileDB.
 */
@Table(name = "metric_parameters")
@Getter
@Setter
public class MetricParameter {
	
	@Id
	@Column(name = "name", type = "String")
    private String name;
	
	@Column(name = "group_name", type = "String")
	private String groupName;
	
	@Column(name = "description", type = "String")
    private String description;
	
	@Column(name = "data_type", type = "String")
    private String dataType;

    public MetricParameter() {
        // Default constructor
    }

    public MetricParameter(String name, String description, String dataType) {
        this.name = name;
        this.description = description;
        this.dataType = dataType;
    }
}
