package com.petko.services;

import com.petko.entities.EmailsEntity;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

public interface IEmailService {
    List<EmailsEntity> getAll(ModelMap modelMap, HttpSession session);

    void addEmail(ModelMap modelMap, String name, String email);

    void deleteEmail(ModelMap modelMap, String idString);

    void /*String*/ getEmailOfReceiver(ModelMap modelMap, String idString);

    void sendMail(ModelMap modelMap, String idString, String subject,
                  String body, File... uploadedFiles);

    void createZipFile(ModelMap modelMap, String applicationLocation, String zipFileName, String... wildCards);
}
