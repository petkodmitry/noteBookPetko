package com.petko.controllers;

import com.petko.entities.EmailsEntity;
import com.petko.services.EmailService;
import com.petko.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Controller
public class MainController {
//    private final String errorMessageAttribute = "errorMessage";
//    private final String infoMessageAttribute = "info";
    //    private final String forwardPageAttribute = Constants.FORWARD_PAGE_ATTRIBUTE;
//    private ModelMap modelMap = new ModelMap();

    @Autowired
    private IUserService userService;
    @Autowired
    private EmailService emailService;

    /*@RequestMapping(value = "/main", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(ModelMap modelMap){
        List<EmailsEntity> emailsList;
        emailsList = emailService.getAll(modelMap);
        modelMap.addAttribute("emailsList", emailsList);
        return "main";
    }*/

    @RequestMapping(value = "/main", method = {RequestMethod.POST, RequestMethod.GET})
    public String main(ModelMap modelMap, HttpSession session, String perPage,
                       @RequestParam(value = "page", required = false) String page,
                       @RequestParam(value = "sortBy", required = false) String sortBy,
                       @RequestParam(value = "orderType", required = false) String orderType,
                       @RequestParam(value = "filterRemove", required = false) String filterRemove,
                       @RequestParam(value = "filterSet", required = false) String filterSet,
                       @RequestParam(value = "filterText", required = false) String filterText){
        HashMap<String, String> filters = (HashMap<String, String>) session.getAttribute("filters");
        filters = filters == null ? new HashMap<>() : filters;
        if (filterSet != null && filterText != null && !filterText.equals("")) {
            filters.put(filterSet, filterText);
        }
        if (filterRemove != null) filters.remove(filterRemove);
        session.setAttribute("filters", filters);

        if (perPage != null) modelMap.addAttribute("perPage", perPage);
        if (page != null) modelMap.addAttribute("page", page);
        if (sortBy != null) modelMap.addAttribute("sortBy", sortBy);
        if (orderType != null) modelMap.addAttribute("orderType", orderType);
        if (filterRemove != null) modelMap.addAttribute("filterRemove", filterRemove);

        List<EmailsEntity> emailsList;
        emailsList = emailService.getAll(modelMap, session);
        modelMap.addAttribute("emailsList", emailsList);
        return "main";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddPage(/*ModelMap modelMap*/){
        return "addEmail";
    }

    @RequestMapping(value = "/addEmail", method = RequestMethod.POST)
    public String addEmail(ModelMap modelMap, String name, String email){
        if (name == null || email == null){
            return "addEmail";
        }
        emailService.addEmail(modelMap, name, email);

//        EmailsEntity entity = new EmailsEntity();
//        entity.setEmail(email);
//        entity.setName(name);
//        if ("".equals(name) || "".equals(email)) {
//            modelMap.addAttribute(errorMessageAttribute, "Поля не могут быть пустыми");
//            modelMap.addAttribute("regData", entity);
//            return "addEmail";
//        }
//        if (isEmailOK(email)) {
//            emailService.add(entity);
//            modelMap.addAttribute(infoMessageAttribute, "Контакт успешно добавлен.");
//        }
//        else {
//            modelMap.addAttribute(errorMessageAttribute, "E-mail не удовлетворяет условиям.");
//            modelMap.addAttribute("regData", entity);
//        }

        return "addEmail";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteEmail(ModelMap modelMap, HttpSession session, String id){
        emailService.deleteEmail(modelMap, id);
        return main(modelMap, session, null, null, null, null, null, null, null);
    }

    /*@RequestMapping(value = "/send", method = RequestMethod.GET)
    public String sendEmail(ModelMap modelMap, HttpSession session, String id){
        emailService.sendMail(modelMap, id, "Petko project and CV", "This message was sent from the test email");
        return main(modelMap, session, null, null, null, null, null, null, null);
    }*/

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public String sendToEmail(ModelMap modelMap, /*HttpSession session,*/ String id){
        /*emailService.sendMail2(modelMap, id);
        return main(modelMap, session, null, null, null, null, null, null, null);*/
        emailService.sendMail2(modelMap, id);
        return "sendMessage";
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public String sendMessage(ModelMap modelMap, HttpSession session, String sendTo, String theme, String body,
                              @RequestParam(value = "upload", required = false) MultipartFile[] upload){
        emailService.sendMail(modelMap, sendTo, theme, body, upload);
        return main(modelMap, session, null, null, null, null, null, null, null);
    }
}
