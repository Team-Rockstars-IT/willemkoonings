package com.wkoonings.rockstarsit.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private static final String USER1_USERNAME = "user1";
  private static final String USER1_PASSWORD = "password";

  private static final String USER2_USERNAME = "user2";
  private static final String USER2_PASSWORD = "password";

  private final Map<String, String> validUserTokens = new ConcurrentHashMap<>();

  public String authenticate(String username, String password) {
    if ((USER1_USERNAME.equals(username) && USER1_PASSWORD.equals(password)) ||
        (USER2_USERNAME.equals(username) && USER2_PASSWORD.equals(password))) {
      final String token = this.generateToken();
      validUserTokens.put(token, username);
      return token;
    } else {
      throw new BadCredentialsException("Invalid username or password");
    }
  }

  public String validateToken(String token) {
    return validUserTokens.get(token);
  }

  public String invalidateToken(String token) {
    return validUserTokens.remove(token);
  }

  private String generateToken() {
    return UUID.randomUUID().toString();
  }
}
