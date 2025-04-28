package com.srirama.tms.respository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.srirama.db.orm.EntityManager;
import com.srirama.tms.entity.MetricParameter;

@Component
public class MetricParameterRepository implements CrudRepository<MetricParameter>{
	
	@Autowired
	private EntityManager entityManager;

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Class<MetricParameter> getEntityClass() {
		return MetricParameter.class;
	}
}
