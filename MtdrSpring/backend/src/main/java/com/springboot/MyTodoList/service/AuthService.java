package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository usuarioRepository;

    public AuthService(UserRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public User authenticate(String email, String password) {
        return usuarioRepository.findByEmailAndPassword(email, password).orElse(null);
    }
}
