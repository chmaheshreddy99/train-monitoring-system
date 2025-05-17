package com.srirama.tms.respository;

import java.lang.reflect.ParameterizedType;

import org.springframework.beans.factory.annotation.Autowired;

import com.srirama.db.orm.EntityManager;

public class BaseRepository<T> implements CrudRepository<T> {

	@Autowired
	private EntityManager entityManager;

	private final Class<T> entityClass;

	@SuppressWarnings("unchecked")
	public BaseRepository() {
		this.entityClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Class<T> getEntityClass() {
		return entityClass;
	}

}
