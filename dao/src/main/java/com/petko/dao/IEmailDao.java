package com.petko.dao;

import com.petko.DaoException;
import com.petko.entities.EmailsEntity;

import java.util.List;
import java.util.Map;

public interface IEmailDao extends IDao<EmailsEntity> {
    Long getTotal() throws DaoException;
    List<EmailsEntity> getAllWithSortAndFilter(int first, int max, String sortBy, String orderType,
                                               Map<String, String> filters) throws DaoException;
}
