package com.srirama.tms.respository;

import java.util.List;

import com.srirama.db.orm.EntityManager;

public interface CrudRepository<T> {

    EntityManager getEntityManager();

    Class<T> getEntityClass();

    default T save(T entity) {
        getEntityManager().save(entity);
        return entity;
    }
    
    default void saveAll(List<T> entity) {
        getEntityManager().saveAll(entity);
    }

    default List<T> findAll() {
        return getEntityManager().findAll(getEntityClass());
    }

    default T findById(String id) {
        return getEntityManager().findById(getEntityClass(), id);
    }

    default T update(T entity) {
        getEntityManager().update(entity);
        return entity;
    }

    default boolean delete(String id) {
        return getEntityManager().delete(getEntityClass(), id);
    }
}

