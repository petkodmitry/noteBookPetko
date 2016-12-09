package com.petko.services;

import com.petko.DaoException;
import com.petko.dao.IDao;
import com.petko.dao.IEmailDao;
import com.petko.entities.EmailsEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.*;

@Service
public class EmailService implements IEmailService {
    private static Logger log = Logger.getLogger(EmailService.class);

    @Autowired
    private IEmailDao emailDao;
    @Autowired
    private IDao baseDao;

    /**
     * gives List of all Users
     * @param modelMap - Map of current http model
     * @return List of all Users
     */
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<EmailsEntity> getAll(ModelMap modelMap) {
        List<EmailsEntity> result;
        String page = (String) modelMap.get("page");

        // for sorting
        String sortBy = (String) modelMap.get("sortBy");
        String orderType = (String) modelMap.get("orderType");

        // for filtering
        Map<String, String> filters = (Map<String, String>) modelMap.get("filters");
        if (filters == null) filters = new HashMap<>();
//        Map<String, String[]> parMap = modelMap.getParameterMap();
//        for (String parameter : parMap.keySet()) {
//            if (parameter.endsWith("Filter")) {
//                String paramToPut = parameter.substring(0, parameter.indexOf("Filter"));
//                String paramValue = parMap.get(parameter)[0];
//                if (!"".equals(paramValue)) filters.put(paramToPut, paramValue);
//            }
//        }
        String filterRemove = (String) modelMap.get("filterRemove");
        if (filterRemove != null) filters.remove(filterRemove);
        modelMap.addAttribute("filters", filters);

        // go ahead
        String perPageString = (String) modelMap.get("perPage");
        Integer newPerPage = perPageString != null ? Integer.parseInt(perPageString) : null;
        Integer oldPerPage = (Integer) modelMap.get("max");
        Integer newMax;
        if (newPerPage != null) {
            newMax = newPerPage;
        }
        else newMax = oldPerPage != null ? oldPerPage : 5;

        try {
            int firstInt;
            if (page == null) {
                Long total = emailDao.getTotal();
                modelMap.addAttribute("total", total);
                firstInt = 0;
            } else {
                Integer pageInt = Integer.parseInt(page);
                Integer newPageInt = getPageDueToNewPerPage(modelMap, pageInt, newPerPage, oldPerPage);
                if (sortBy != null && orderType != null) {
                    modelMap.addAttribute("sortBy", sortBy);
                    modelMap.addAttribute("orderType", orderType);
                }
                firstInt = (newPageInt - 1) * newMax;
            }
            modelMap.addAttribute("totalToShow", emailDao.getAllWithSortAndFilter(0,  ((Long) modelMap.get("total")).intValue(), sortBy, orderType, filters).size());
            result = emailDao.getAllWithSortAndFilter(firstInt, newMax, sortBy, orderType, filters);
            modelMap.addAttribute("max", newMax);
        } catch (DaoException e) {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * Re-estimates current page due to perPage parameter changes
     * @param modelMap - Map of current http model
     * @param page - current page (not re-estimated)
     * @param newPerPage - a new perPage parameter
     * @param oldPerPage - perPage parameter which is saved in httpSession
     * @return the correct page due to perPage changes
     */
    private Integer getPageDueToNewPerPage(ModelMap modelMap, Integer page, Integer newPerPage, Integer oldPerPage) {
        Integer result;
        Integer totalToShow = (Integer) modelMap.get("totalToShow");
        if ((newPerPage == null || oldPerPage == null) || (newPerPage.equals(oldPerPage))) {
            result = page;
        } else if (newPerPage > oldPerPage) {
            result = changeAndGiveCurrentPage(page, totalToShow, newPerPage, oldPerPage, true);
        } else {
            result = changeAndGiveCurrentPage(page, totalToShow, newPerPage, oldPerPage, false);
        }
        modelMap.addAttribute("page", result);
        return result;
    }

    /**
     * Re-estimates current page due to perPage parameter changes
     * @param page - current page (not re-estimated)
     * @param totalToShow - the whole amount of records (including filters)
     * @param newPerPage - a new perPage parameter
     * @param oldPerPage - perPage parameter which is saved in httpSession
     * @param isMoreRecords - is new perPage bigger than current
     * @return the correct page due to perPage changes
     */
    private Integer changeAndGiveCurrentPage(Integer page, Integer totalToShow, Integer newPerPage,
                                             Integer oldPerPage, boolean isMoreRecords) {
        Integer result;
        int temp = page * oldPerPage / newPerPage;
        int rest = page * oldPerPage % newPerPage;
        if (isMoreRecords) {
            result = rest != 0 ? temp + 1 : temp;
        } else result = rest != 0 ? temp - 1 : temp;
        rest =  totalToShow % newPerPage;
        Integer endPage = rest != 0 ? (totalToShow - rest) / newPerPage + 1 : totalToShow / newPerPage;
        if (endPage < result) result = endPage;
        return result;
    }
}
