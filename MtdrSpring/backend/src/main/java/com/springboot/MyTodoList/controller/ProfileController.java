package com.springboot.MyTodoList.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping("/admin-info")
    public ResponseEntity<String> getAdminInfo() {
        return ResponseEntity.ok("This is confidential info for ADMIN users only.");
    }

    @GetMapping("/user-info")
    public ResponseEntity<String> getUserInfo() {
        return ResponseEntity.ok("Welcome USER, here's your info.");
    }
}
