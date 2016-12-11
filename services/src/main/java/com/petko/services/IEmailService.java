package com.petko.services;

import com.petko.entities.EmailsEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface IEmailService {
    List<EmailsEntity> getAll(ModelMap modelMap, HttpSession session);

    void addEmail(ModelMap modelMap, String name, String email);

    void deleteEmail(ModelMap modelMap, String idString);

    void getEmailOfReceiver(ModelMap modelMap, String idString);

    void sendMail(ModelMap modelMap, String idString, String subject,
                  String body, MultipartFile[] uploadedFiles);
}
