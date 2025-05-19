package com.srirama.tms.config;

import java.util.List;

import org.springframework.stereotype.Component;

import com.srirama.tms.entity.MetricParameter;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class DataLoggerConfig {
	
	private boolean isConfigChanged;
		
	private List<MetricParameter> selectedMetricParams;
	
}
