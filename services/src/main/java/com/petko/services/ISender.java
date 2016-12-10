package com.petko.services;

import javax.mail.MessagingException;

public interface ISender {
    void sendMail(String to, String subject, String body) throws MessagingException;
}
