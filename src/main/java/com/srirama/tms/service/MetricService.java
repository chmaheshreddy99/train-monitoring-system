package com.srirama.tms.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.srirama.tms.connector.FileMessageSender;
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
	
	@Autowired
	private FileMessageSender messageSender;

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

	public void saveParameterPreferences(List<MetricParameter> metricParameters, String preferenceName) {
	   
	    List<MetricParameterPreferences> preferences = metricParameters.stream()
	        .map(metric -> {
	            MetricParameterPreferences preference = new MetricParameterPreferences();
	            preference.setName(preferenceName);
	            preference.setDescription(preferenceName);
	            preference.setMetricParameterName(metric.getName());
	            preference.setMetricGroupName(metric.getGroupName());
	            return preference;
	        })
	        .toList();

	    metricParameterPreferencesRepository.saveAll(preferences);
	}
	
	public boolean send(List<MetricParameter> metricParameters) {
		String message = "";
		for (MetricParameter param : metricParameters) {
			message = message + param.getDataType() + "|";
		}
		try {
			messageSender.writeMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<String> getAllConfigNames() {
		return metricParameterPreferencesRepository.findAll()
				.stream()
				.map(s -> s.getName())
				.distinct()
				.toList();
	}
	
	public List<MetricParameter> getAllMetricParametersByConfigName(String configName) {

		return metricParameterPreferencesRepository.findByPreferenceName(configName)
				.stream()
				.map(metricParam -> metricParameterRepository.findById(metricParam.getMetricParameterName()))
				.distinct()
				.toList();
	}
}
