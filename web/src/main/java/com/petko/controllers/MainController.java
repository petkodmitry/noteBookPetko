package com.petko.controllers;

import com.petko.entities.EmailsEntity;
import com.petko.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private EmailService emailService;

    @RequestMapping(value = {"/main", "/noteBookPetko", "/"}, method = {RequestMethod.POST, RequestMethod.GET})
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
        if (sortBy != null) session.setAttribute("sortBy", sortBy);
        if (orderType != null) session.setAttribute("orderType", orderType);
        if (filterRemove != null) modelMap.addAttribute("filterRemove", filterRemove);

        List<EmailsEntity> emailsList;
        emailsList = emailService.getAll(modelMap, session);
        modelMap.addAttribute("emailsList", emailsList);
        return "main";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAddPage(){
        return "addEmail";
    }

    @RequestMapping(value = "/addEmail", method = RequestMethod.POST)
    public String addEmail(ModelMap modelMap, String name, String email){
        if (name == null || email == null){
            return "addEmail";
        }
        emailService.addEmail(modelMap, name, email);
        return "addEmail";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteEmail(ModelMap modelMap, HttpSession session, String id){
        emailService.deleteEmail(modelMap, id);
        return main(modelMap, session, null, null, null, null, null, null, null);
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public String sendToEmail(ModelMap modelMap, String id){
        emailService.getEmailOfReceiver(modelMap, id);
        return "sendMessage";
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public String sendMessage(ModelMap modelMap, HttpSession session, String sendTo, String subject, String body){
        String cvFileName = "PetkoCV.docx";
        String zipFileName = "noteBook-Петько.zip";
        String[] wildCards = {"tmp", ".classpath", ".project", ".idea", ".iml",
                ".checkstyle", "build", "target", "bin", ".settings", ".git",
                "out", "build", "target", ".xlsx", ".docx", ".zip"};
        String applicationLocation = emailService.getAppDirectory(modelMap);

        emailService.createZipFile(modelMap, applicationLocation, zipFileName, wildCards);
        File cvFile = new File(applicationLocation + cvFileName);
        if (!cvFile.exists()) cvFile = null;
        File zipFile = new File(applicationLocation + zipFileName);
        if (!zipFile.exists()) zipFile = null;
        emailService.sendMail(modelMap, sendTo, subject, body,
                cvFile, zipFile);

        return main(modelMap, session, null, null, null, null, null, null, null);
    }
}
