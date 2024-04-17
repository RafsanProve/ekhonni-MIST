package com.dsi.backend.controller;

import com.dsi.backend.model.Notification;
import com.dsi.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchNotification(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        return ResponseEntity.ok(notificationService.fetchNotification(token));
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<?> clearAllNotification(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        return ResponseEntity.ok(notificationService.clearAllNotification(token));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> clearNotification(@PathVariable Long id){
        return ResponseEntity.ok(notificationService.clearNotification(id));
    }
}
