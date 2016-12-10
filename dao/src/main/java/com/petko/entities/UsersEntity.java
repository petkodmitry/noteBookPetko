package com.petko.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UsersEntity extends BaseEntity {
    private int userId;
    private String login;
    private String password;
    private Set<EmailsEntity> emailsEntitySet = new HashSet<>();

    @Id
    @Column(name = "u_id", nullable = false, unique = true)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "login", nullable = false, length = 50, unique = true)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 50)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToMany(mappedBy = "user")
    public Set<EmailsEntity> getEmailsEntitySet() {
        return emailsEntitySet;
    }

    public void setEmailsEntitySet(Set<EmailsEntity> emailsEntitySet) {
        this.emailsEntitySet = emailsEntitySet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersEntity)) return false;

        UsersEntity that = (UsersEntity) o;

        if (getUserId() != that.getUserId()) return false;
        if (getLogin() != null ? !getLogin().equals(that.getLogin()) : that.getLogin() != null) return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        return getEmailsEntitySet() != null ? getEmailsEntitySet().equals(that.getEmailsEntitySet()) : that.getEmailsEntitySet() == null;

    }

    @Override
    public int hashCode() {
        int result = getUserId();
        result = 31 * result + (getLogin() != null ? getLogin().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getEmailsEntitySet() != null ? getEmailsEntitySet().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("User [userId=%d, login=%s, password=%s]",
                userId, login, password);
    }
}
