package com.lolgap.project.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicAuthExampleController {

  @GetMapping("/public/secureAPI")
  public ResponseEntity securedApi(@RequestHeader HttpHeaders headers) {
    if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
      String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
      if (authorizationHeader.startsWith("Basic ")) {
        return new ResponseEntity<>("Authentication passed", HttpStatus.OK);
      }
    }
    return new ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED);
  }
}