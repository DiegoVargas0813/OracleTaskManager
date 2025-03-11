package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAll(){
        List<User> users = userRepository.findAll();
        return users;
    }

    public Optional<User> getUserById(int id){
        return userRepository.findById(id);
    }
}
