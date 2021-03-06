package com.petko.entities;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "emails")
public class EmailsEntity extends BaseEntity {
    private int emailId;
    private String name;
    private String email;

    @Id
    @Column(name = "m_id", nullable = false, unique = true)
    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "email", nullable = false, length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailsEntity)) return false;

        EmailsEntity that = (EmailsEntity) o;

        if (getEmailId() != that.getEmailId()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return  (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null);
    }

    @Override
    public int hashCode() {
        int result = getEmailId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Email [emailId=%d, name=%s, email=%s]",
                emailId, name, email);
    }
}
