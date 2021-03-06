package com.example.library.service;

import com.example.library.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.library.models.enums.Role;

public interface UserService extends UserDetailsService {
    User register(String username, String password, String repeatPassword, String name, String surname, Role role);
}
