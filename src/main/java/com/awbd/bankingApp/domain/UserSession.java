package com.awbd.bankingApp.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Setter
@Getter
@ToString
@Component
@SessionScope
public class UserSession {
    private String uid;
    private boolean isAdmin;

    public UserSession() {

    }

    public UserSession(String uid) {
        this.uid = uid;
    }

    public UserSession(String uid, boolean isAdmin) {
        this.uid = uid;
        this.isAdmin = isAdmin;
    }
}