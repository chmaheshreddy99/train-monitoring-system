package com.srirama.tms.entity;

import com.srirama.db.annotations.Id;
import com.srirama.db.annotations.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "metric_parameter_preferences")
@Getter
@Setter
@ToString
public class MetricParameterPreferences {
	
	@Id
	private String name;
	
	private String description;
	
	private String metricParameterName;
	
	private String metricGroupName;

}
