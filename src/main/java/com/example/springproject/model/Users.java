package com.example.springproject.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.Objects;

//get set constructor tostring hashcode equal

//@Service // 是 Spring 的 Bean 注解，放在 model/DTO 上是错的，Users 只是一个数据类，不需要任何注解。
public class Users {
    @NotBlank(message="username cannot be empty or null")
    @Size(min=3, max=20,message="size should be 3-20 characters")
    private String userName;
    private String updatedUserName;
    @Email(message="invalid email format")
    private String email;
    private String updatedEmail;
    @NotBlank
    private String password;
    private String updatedPassword;
    private String role;

    public Users(){}

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUpdatedUserName() {
        return updatedUserName;
    }

    public void setUpdatedUserName(String updatedUserName) {
        this.updatedUserName = updatedUserName;
    }

    public String getUpdatedEmail() {
        return updatedEmail;
    }

    public String getUpdatedPassword() {
        return updatedPassword;
    }

    public void setUpdatedEmail(String updatedEmail) {
        this.updatedEmail = updatedEmail;
    }

    public void setUpdatedPassword(String updatedPassword) {
        this.updatedPassword = updatedPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
