package com.petko.services;

import com.petko.DaoException;
import com.petko.dao.IDao;
import com.petko.dao.IEmailDao;
import com.petko.entities.EmailsEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
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
     *
     * @param modelMap - Map of current http model
     * @return List of all Users
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = DaoException.class)
    public List<EmailsEntity> getAll(ModelMap modelMap, HttpSession session) {
        List<EmailsEntity> result;
        String page = (String) modelMap.get("page");

        // for sorting
        String sortBy = (String) session.getAttribute("sortBy");
        String orderType = (String) session.getAttribute("orderType");
        // for filtering
        Map<String, String> filters = (Map<String, String>) session.getAttribute("filters");

        String perPageString = (String) modelMap.get("perPage");
        Integer newPerPage = perPageString != null ? Integer.parseInt(perPageString) : null;
        Integer oldPerPage = (Integer) session.getAttribute("max");
        Integer newMax;
        if (newPerPage != null) {
            newMax = newPerPage;
        } else newMax = oldPerPage != null ? oldPerPage : 5;

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
                    session.setAttribute("sortBy", sortBy);
                    session.setAttribute("orderType", orderType);
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
     *
     * @param modelMap   - Map of current http model
     * @param page       - current page (not re-estimated)
     * @param newPerPage - a new perPage parameter
     * @param oldPerPage - perPage parameter which is saved in httpSession
     * @return the correct page due to perPage changes
     */
    private Integer getPageDueToNewPerPage(ModelMap modelMap, HttpSession session, Integer page,
                                           Integer newPerPage, Integer oldPerPage) {
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
     *
     * @param page          - current page (not re-estimated)
     * @param totalToShow   - the whole amount of records (including filters)
     * @param newPerPage    - a new perPage parameter
     * @param oldPerPage    - perPage parameter which is saved in httpSession
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
        rest = totalToShow % newPerPage;
        Integer endPage = rest != 0 ? (totalToShow - rest) / newPerPage + 1 : totalToShow / newPerPage;
        if (endPage < result) result = endPage;
        return result;
    }

    /**
     * adds a new contact into our notebook
     *
     * @param modelMap - Map of current http model
     * @param name     - name of the Contact
     * @param email    - email of the Contact
     */
    @Override
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
            try {
                baseDao.save(entity);
                modelMap.addAttribute(infoMessageAttribute, "Контакт успешно добавлен.");
            } catch (DaoException e) {/*NOP*/}
        } else {
            modelMap.addAttribute(errorMessageAttribute, "E-mail не удовлетворяет условиям.");
            modelMap.addAttribute("regData", entity);
        }
    }

    private boolean isEmailOK(String email) {
        return email.matches("[a-zA-Z0-9.%+-_]+@[a-zA-Z0-9.%+-]+\\.[a-zA-Z]{2,}");
    }

    /**
     * deletes the contact from our notebook
     *
     * @param modelMap - Map of current http model
     * @param idString - the ID of the contact, String version
     */
    @Override
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
        } catch (DaoException e) {/*NOP*/}
    }

    /**
     * getting the email of the contact by its ID
     *
     * @param modelMap - Map of current http model
     * @param idString - the ID of the contact, String version
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = {DaoException.class, NumberFormatException.class})
    public void getEmailOfReceiver(ModelMap modelMap, String idString) {
        try {
            int id = Integer.parseInt(idString);
            EmailsEntity entity = emailDao.getById(id);
            modelMap.put("email", entity.getEmail());
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать id.");
        } catch (DaoException e) {/*NOP*/}
    }

    /**
     * @param modelMap      - Map of current http model
     * @param sendTo        - the ID of the contact, String version
     * @param subject       of the mail
     * @param body          of the mail
     * @param uploadedFiles - files to be put into the email
     */
    @Override
    public void sendMail(ModelMap modelMap, String sendTo, String subject,
                         String body, File... uploadedFiles) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(body);
            for (File file : uploadedFiles) {
                if (file != null) helper.addAttachment(file.getName(), file);
            }
            mailSender.send(message);
            modelMap.addAttribute(infoMessageAttribute, "Письмо успешно отправлено на почту " + sendTo + "!");
        } catch (MessagingException e) {
            modelMap.addAttribute(errorMessageAttribute, "Ошибка работы с объектом сообщения.");
        } catch (NumberFormatException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать id контакта.");
        }
    }

    public String getAppDirectory(ModelMap modelMap) {
        String applicationLocation = "";
        try {
            URL location = this.getClass().getProtectionDomain().getCodeSource().getLocation();
            String currentRelativeDir = "web\\target\\";
            applicationLocation = URLDecoder.decode(location.getFile().substring(1).replace('/', File.separatorChar), Charset.defaultCharset().name());
            applicationLocation = applicationLocation.substring(0, applicationLocation.indexOf(currentRelativeDir));
        } catch (UnsupportedEncodingException e) {
            modelMap.addAttribute(errorMessageAttribute, "Невозможно распознать директорию с проектом.");
        }
        return applicationLocation;
    }

    @Transactional(readOnly = true, rollbackFor = {IOException.class})
    public void createZipFile(ModelMap modelMap, String applicationLocation, String zipFileName, String[] wildCards) {
        try (ZipOutputStream zout = new ZipOutputStream(
                new FileOutputStream(applicationLocation + zipFileName))) {
            // filter for file which are NOT of endings referred in wildCards
            FilenameFilter nameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    for (String filters : wildCards) {
                        if (name.toLowerCase().endsWith(filters)) return false;
                    }
                    return true;
                }
            };
            Map<String, ByteArrayOutputStream> allFilesByFilter =
                    createZipFile(applicationLocation, nameFilter, applicationLocation);
            for (Map.Entry<String, ByteArrayOutputStream> entry : allFilesByFilter.entrySet()) {
                zout.putNextEntry(new ZipEntry(entry.getKey()));
                ByteArrayOutputStream value = entry.getValue();
                if (value != null) {
                    zout.write(value.toByteArray());
                }
                zout.closeEntry();
            }
        } catch (IOException e) {
            modelMap.addAttribute(errorMessageAttribute, "Ошибка в чтении файла(ов).");
        }
    }

    private Map<String, ByteArrayOutputStream> createZipFile(String applicationAbsoluteDir,
                                                             FilenameFilter nameFilter,
                                                             String currentAbsoluteDir) throws IOException {
        Map<String, ByteArrayOutputStream> result = new HashMap<>();

        File parentDir = new File(currentAbsoluteDir);

        String[] fileList = parentDir.list(nameFilter);

        for (String fileInDir : fileList) {
            String newAbsoluteDir = currentAbsoluteDir + "\\" + fileInDir;
            Path path = Paths.get(newAbsoluteDir);
            if (Files.isDirectory(path))
                result.putAll(createZipFile(applicationAbsoluteDir, nameFilter, newAbsoluteDir));
            else {
                try (FileInputStream fis = new FileInputStream(newAbsoluteDir)) {
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(buffer);
                    String newFileInDir = newAbsoluteDir.substring(applicationAbsoluteDir.length(), newAbsoluteDir.length());
                    result.put(newFileInDir, baos);
                }
            }
        }
        return result;
    }
}
