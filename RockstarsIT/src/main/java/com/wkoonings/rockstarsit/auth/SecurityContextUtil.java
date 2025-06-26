package com.wkoonings.rockstarsit.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUtil {

  public static String getLoggedInUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      return authentication.getName();
    }

    return null;
  }

  public static String getLoggedInToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      return (String) authentication.getCredentials();
    }

    return null;
  }
}
