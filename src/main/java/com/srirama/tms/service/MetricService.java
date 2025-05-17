package com.srirama.tms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.srirama.tms.entity.MetricParameter;
import com.srirama.tms.entity.MetricParameterPreferences;
import com.srirama.tms.respository.MetricParameterPreferencesRepository;
import com.srirama.tms.respository.MetricParameterRepository;

@Service
public class MetricService {

	@Autowired
	private MetricParameterRepository metricParameterRepository;

	@Autowired
	private MetricParameterPreferencesRepository metricParameterPreferencesRepository;

	public List<MetricParameter> getMetricParams() {
		return metricParameterRepository.findAll();
	}

	public List<MetricParameter> getMetricsByGroup(String groupName) {
		return metricParameterRepository.findAll().stream().filter(t -> t.getGroupName().equals(groupName)).toList();
	}

	public List<String> getAllMetricGroups() {
		return metricParameterRepository.findAll().stream().map(MetricParameter::getGroupName).distinct().sorted()
				.toList();
	}

	public void saveParameterPreferences(List<MetricParameter> metricParameters) {
	   
		final String preferenceName = "Preference-1";

	    List<MetricParameterPreferences> preferences = metricParameters.stream()
	        .map(metric -> {
	            MetricParameterPreferences preference = new MetricParameterPreferences();
	            preference.setName(preferenceName);
	            preference.setMetricParameterName(metric.getName());
	            preference.setMetricGroupName(metric.getGroupName());
	            return preference;
	        })
	        .toList();

	    metricParameterPreferencesRepository.saveAll(preferences);
	}


}
