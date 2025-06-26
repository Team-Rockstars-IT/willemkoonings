package com.wkoonings.rockstarsit.controllers;

import com.wkoonings.rockstarsit.api.AuthApi;
import com.wkoonings.rockstarsit.auth.AuthService;
import com.wkoonings.rockstarsit.auth.SecurityContextUtil;
import com.wkoonings.rockstarsit.dto.LoginRequest;
import com.wkoonings.rockstarsit.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;

  @Override
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> userLogin(@RequestBody final LoginRequest request) {
    String token = authService.authenticate(request.getUsername(), request.getPassword());
    return ResponseEntity.ok(new LoginResponse(token, "Authentication successful"));
  }

  @Override
  @PostMapping("/logout")
  public ResponseEntity<Void> userLogout() {
    authService.invalidateToken(SecurityContextUtil.getLoggedInToken());
    return ResponseEntity.status(201).build();
  }
}
