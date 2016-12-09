package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.BaseEntity;

public interface IDao<T extends BaseEntity> {
    /**
     * gives Entity by id
     * @param id - id of looking Entity
     * @return Entity by id
     */
    T getById(int id) throws DaoException;

    /**
     * adds entity in database
     * @param entity - new Entity
     */
    void save(T entity) throws DaoException;

    /**
     * updates entity in database
     * @param entity - Entity
     */
    void update(T entity) throws DaoException;

    /**
     * deletes Entity
     * @param entity - Entity to be deleted
     */
    void delete(T entity) throws DaoException;
}
