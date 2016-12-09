package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.BaseEntity;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;

@Repository
public class BaseDao<T extends BaseEntity> implements IDao<T>{
    private static Logger log = Logger.getLogger(BaseDao.class);
    @Autowired
    protected SessionFactory sessionFactory;
    protected Session session;

    @Override
    public T getById(int id) throws DaoException {
        log.info("Get ENTITY by id: " + id);
        T entity;
        try {
            session = sessionFactory.getCurrentSession();
            entity = (T) session.get(getPersistentClass(), id);
            log.info("get() clazz: " + entity);
        } catch (HibernateException | ClassCastException e) {
            String message = "Error get() " + getPersistentClass().getSimpleName() + " in BaseDao.";
            log.error(message + e);
            throw new DaoException(message);
        }
        return entity;
    }

    @Override
    public void save(T entity) throws DaoException {
        try {
            log.info("save(): " + entity);
            session = sessionFactory.getCurrentSession();
            session.save(entity);
        } catch (HibernateException | IllegalArgumentException e) {
            String message = "Error save() " + entity + " in BaseDao.";
            log.error(message + e);
            throw new DaoException(message);
        }
    }

    @Override
    public void update(T entity) throws DaoException {
        try {
            log.info("update(): " + entity);
            session = sessionFactory.getCurrentSession();
            session.update(entity);
        } catch (HibernateException | IllegalArgumentException e) {
            String message = "Error update() " + entity + " in BaseDao.";
            log.error(message + e);
            throw new DaoException(message);
        }
    }

    @Override
    public void delete(T entity) throws DaoException {
        try {
            log.info("delete(): " + entity);
            session = sessionFactory.getCurrentSession();
            session.delete(entity);
        } catch (HibernateException | IllegalArgumentException e) {
            String message = "Error delete() " + entity + " in BaseDao.";
            log.error(message + e);
            throw new DaoException(message);
        }
    }

    /**
     * Defines correct class which extends BaseEntity class
     * @return exactly used persistant class
     */
    protected Class getPersistentClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
