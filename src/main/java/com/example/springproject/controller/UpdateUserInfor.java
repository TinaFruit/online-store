package com.example.springproject.controller;

import com.example.springproject.service.UpdateUserSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.springproject.model.Users;

@RestController
public class UpdateUserInfor {

    @Autowired
    private UpdateUserSer updateUserSer;

    @PutMapping("/login/updateUserName")
    public ResponseEntity<String> updateUserName(@RequestBody Users user){
        if( updateUserSer.updateUserNameSer(user)){
            return ResponseEntity.ok("updated successfully");
        }
        return ResponseEntity.status(401).body("update failed");

    }
    @PutMapping("/login/updatePasswordOrEmail")
    public ResponseEntity<String> updateUserPasswordOrEmail(@RequestBody Users user){
        if( updateUserSer.updateUserPasswordOrEmailServ(user)){
            return ResponseEntity.ok("updated successfully");
        }
        return ResponseEntity.status(401).body("update failed");
    }
    }
