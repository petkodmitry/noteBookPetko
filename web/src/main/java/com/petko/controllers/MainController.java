package com.petko.controllers;

import com.petko.entities.EmailsEntity;
import com.petko.services.EmailService;
import com.petko.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class MainController {
//    private final String errorMessageAttribute = "errorMessage";
    //    private final String forwardPageAttribute = Constants.FORWARD_PAGE_ATTRIBUTE;
//    private ModelMap modelMap = new ModelMap();

    @Autowired
    private IUserService userService;
    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/main", method = {RequestMethod.POST, RequestMethod.GET})
    public String login(ModelMap modelMap){
        List<EmailsEntity> emailsList;
        emailsList = emailService.getAll(modelMap);
        modelMap.addAttribute("emailsList", emailsList);
        return "main";
    }
}
