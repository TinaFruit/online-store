package com.example.springproject.service;

import com.example.springproject.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class LogoutSer {

   public final static HashSet<String> blacklist =  new HashSet<>();

    public boolean logoutSer(  String token ){
       return blacklist.add(token);
    }

}
