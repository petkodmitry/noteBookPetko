package com.petko.services;

import com.petko.DaoException;
import com.petko.dao.IDao;
import com.petko.dao.IEmailDao;
import com.petko.entities.EmailsEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.ServiceManager;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class EmailService implements IEmailService {
    private static Logger log = Logger.getLogger(EmailService.class);
    private final String errorMessageAttribute = "errorMessage";
    private final String infoMessageAttribute = "info";

    @Autowired
    private IEmailDao emailDao;
    @Autowired
    private IDao baseDao;
    @Autowired
    private JavaMailSender mailSender;

    /**
     * gives List of all Users
     * @param modelMap - Map of current http model
     * @return List of all Users
     */
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<EmailsEntity> getAll(ModelMap modelMap, HttpSession session) {
        List<EmailsEntity> result;
        String page = (String) modelMap.get("page");

        // for sorting
        String sortBy = (String) modelMap.get("sortBy");
        String orderType = (String) modelMap.get("orderType");
        // for filtering
        Map<String, String> filters = (Map<String, String>) session.getAttribute("filters");

        String perPageString = (String) modelMap.get("perPage");
        Integer newPerPage = perPageString != null ? Integer.parseInt(perPageString) : null;
        Integer oldPerPage = (Integer) session.getAttribute("max");
        Integer newMax;
        if (newPerPage != null) {
            newMax = newPerPage;
        }
        else newMax = oldPerPage != null ? oldPerPage : 5;

        try {
            int firstInt;
            if (page == null) {
                Long total = emailDao.getTotal();
                session.setAttribute("total", total);
                firstInt = 0;
            } else {
                Integer pageInt = Integer.parseInt(page);
                Integer newPageInt = getPageDueToNewPerPage(modelMap, session, pageInt, newPerPage, oldPerPage);
                if (sortBy != null && orderType != null) {
                    modelMap.addAttribute("sortBy", sortBy);
                    modelMap.addAttribute("orderType", orderType);
                }
                firstInt = (newPageInt - 1) * newMax;
            }
            Long totalLong = (Long) session.getAttribute("total");
            int total = totalLong.intValue();
            session.setAttribute("totalToShow", emailDao.getAllWithSortAndFilter(0, total, sortBy, orderType, filters).size());
            result = emailDao.getAllWithSortAndFilter(firstInt, newMax, sortBy, orderType, filters);
            session.setAttribute("max", newMax);
            log.info("getAll e-mails (commit)");
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
    private Integer getPageDueToNewPerPage(ModelMap modelMap, HttpSession session, Integer page, Integer newPerPage, Integer oldPerPage) {
        Integer result;
        Integer totalToShow = (Integer) session.getAttribute("totalToShow");
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

    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void addEmail(ModelMap modelMap, String name, String email) {
        EmailsEntity entity = new EmailsEntity();
        entity.setEmail(email);
        entity.setName(name);
        if ("".equals(name) || "".equals(email)) {
            modelMap.addAttribute(errorMessageAttribute, "Поля не могут быть пустыми.");
            modelMap.addAttribute("regData", entity);
        }
        if (isEmailOK(email)) {
//            add(entity);
            try {
                baseDao.save(entity);
                modelMap.addAttribute(infoMessageAttribute, "Контакт успешно добавлен.");
            } catch (DaoException e) {/*NOP*/}
        }
        else {
            modelMap.addAttribute(errorMessageAttribute, "E-mail не удовлетворяет условиям.");
            modelMap.addAttribute("regData", entity);
        }
    }

    private boolean isEmailOK(String email) {
        return email.matches("[a-zA-Z0-9.%+-_]+@[a-zA-Z0-9.%+-]+\\.[a-zA-Z]{2,}");
    }

    @Transactional(readOnly = false, rollbackFor = DaoException.class)
    public void deleteEmail(ModelMap modelMap, String idString) {
        try {
            int id = Integer.parseInt(idString);
            EmailsEntity entity = emailDao.getById(id);
            if (entity == null) {
                modelMap.addAttribute(errorMessageAttribute, "Невозможно получить объект контакта.");
            } else {
                emailDao.delete(entity);
                modelMap.addAttribute(infoMessageAttribute, "Контакт успешно удалён.");
            }
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать id.");
        } catch (DaoException e){/*NOP*/}
    }

//    @Override
    @Transactional(readOnly = false, rollbackFor = {IOException.class, MessagingException.class, NumberFormatException.class})
    public void sendMail(ModelMap modelMap, String idString, String subject,
                         String body, MultipartFile[] uploadedFiles) {
        try {
//            int id = Integer.parseInt(idString);
//            EmailsEntity entity = emailDao.getById(id);
//            String to = entity.getEmail();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

//            Path projectZip = createProjectZipFile();
//            ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream());
//            ZipInputStream zis = new ZipInputStream(Files.newInputStream());

//            helper.setTo(to);
            helper.setTo(idString);
            helper.setSubject(subject);
            helper.setText(body);

//            FileOpenService openService = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");
//            String[] params = {"txt", "all"};
//            FileContents contents = openService.openFileDialog("D:/", params);

//            FileDialog dialog=new FileDialog(new Frame(),"Open file", FileDialog.LOAD);
//            dialog.setVisible(true);
//            contents.getInputStream();
//            InputStreamSource source = new InputStreamResource(contents.getInputStream());

            for (MultipartFile file : uploadedFiles) {
//                InputStreamResource source = new InputStreamResource(file.getInputStream());
//                File source = new BufferedInputStream(new ByteArrayInputStream(file.getBytes()));
                File tempFile = Files.createTempFile(null, null).toFile();
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(file.getBytes());
                fos.close();
                String fileName = file.getOriginalFilename();
                helper.addAttachment(fileName, tempFile);

            }

            mailSender.send(message);
            modelMap.addAttribute(infoMessageAttribute, "Письмо успешно отправлено на почту " + idString + " .");
        } catch (IOException e) {
            modelMap.addAttribute(errorMessageAttribute, "Ошибка работы с файлом вложения.");
        } catch (MessagingException e) {
            modelMap.addAttribute(errorMessageAttribute, "Ошибка работы с объектом сообщения.");
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать id контакта.");
        }
    }

    public void openDocument() {
        FileDialog dialog=new FileDialog(new Frame(),"Open file", FileDialog.LOAD);
        dialog.setVisible(true);
    }

    /*private Path createProjectZipFile() {
        try {
            String projectDir = currentDir2();
            File fileResult = File.createTempFile("petko", "project");
//            ZipFile zipFile = new ZipFile(fileResult);
            Path pathResult = fileResult.toPath();
            ZipOutputStream result = new ZipOutputStream(Files.newOutputStream(pathResult));
            return pathResult;
        } catch (IOException e) {
            return null;
        }
    }*/

    @Transactional(readOnly = false, rollbackFor = {IOException.class, MessagingException.class, NumberFormatException.class})
    public void sendMail2(ModelMap modelMap, String idString) {
        try {
            int id = Integer.parseInt(idString);
            EmailsEntity entity = emailDao.getById(id);
            modelMap.put("email", entity.getEmail());
        } catch (NumberFormatException | DaoException e) {
            /*NOP*/
        }
    }
}
