package com.srirama.tms.respository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.srirama.tms.entity.MetricParameterPreferences;

@Component
public class MetricParameterPreferencesRepository extends BaseRepository<MetricParameterPreferences> {
	
	public List<MetricParameterPreferences> findByPreferenceName(String preferenceName) {
		return findAll().stream().filter(t -> t.getName().equals(preferenceName)).toList();
	}

}
