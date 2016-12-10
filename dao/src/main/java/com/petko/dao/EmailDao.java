package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.EmailsEntity;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class EmailDao extends BaseDao<EmailsEntity> implements IEmailDao {
    private static Logger log = Logger.getLogger(EmailDao.class);

    /**
     * gives a list of all elements in the DB
     * @return List of all elements
     * @throws DaoException
     */
    @Override
    public Long getTotal() throws DaoException{
        Long result;
        try {
            session = sessionFactory.getCurrentSession();
            String hql = "SELECT count(id) FROM " + getPersistentClass().getSimpleName();
            Query query = session.createQuery(hql);
            result = (Long) query.uniqueResult();
            log.info("getTotal() " + getPersistentClass().getSimpleName() + ". Count=" + result);
        } catch (HibernateException e) {
            String message = "Error getTotal() " + getPersistentClass().getSimpleName() + " in EmailsDao";
            log.error(message + e);
            throw new DaoException(message);
        }
        return result;
    }

    /**
     *
     * @param first - the first record for a page
     * @param max - max count of elements for pagination
     * @return  List of Entities considering given options
     * @throws DaoException
     */
    @Override
    public List<EmailsEntity> getAllWithSortAndFilter(int first, int max, String sortBy, String orderType, Map<String, String> filters) throws DaoException {
        List<EmailsEntity> result;
        try {
            session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(getPersistentClass());
            if (sortBy != null && orderType != null) {
                criteria = orderType.equals("asc") ? criteria.addOrder(Order.asc(sortBy).ignoreCase())
                        : criteria.addOrder(Order.desc(sortBy).ignoreCase());
            }
            for (String filter : filters.keySet()) {
                switch (filter) {
                    case "emailId":
                        criteria.add(Restrictions.sqlRestriction(" m_id LIKE '%" + filters.get(filter) + "%' "));
                        break;
                    default:
                        criteria.add(Restrictions.ilike(filter, "%" + filters.get(filter) + "%"));
                        break;
                }
            }
            criteria.setFirstResult(first);
            criteria.setMaxResults(max);
            result = criteria.list();
            log.info("getAllWithSortAndFilter() " + getPersistentClass().getName() + ". Count=" + result.size());
        } catch (HibernateException e) {
            String message = "Error getAllWithSortAndFilter " + getPersistentClass().getSimpleName() + " in EmailsDao";
            log.error(message + e);
            throw new DaoException(message);
        }
        return result;
    }
}
