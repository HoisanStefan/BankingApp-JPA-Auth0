package com.awbd.bankingApp.utils;

import com.auth0.exception.Auth0Exception;

import java.util.Map;

public class UserChecker {
    public static boolean isAdmin(Map<String, Object> metadata) throws Auth0Exception {
        if (metadata != null) {
            if (metadata.containsKey("admin")) {
                return metadata.get("admin").equals("true");
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
