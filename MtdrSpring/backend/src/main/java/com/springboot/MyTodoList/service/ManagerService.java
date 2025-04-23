package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;

    public Manager createManager(Manager manager){
        return managerRepository.save(manager);
    }

    public List<Manager> getAllManagers(){
        return managerRepository.findAll();
    }

    public Optional<Manager> getManagerById(int id){
        return managerRepository.findById(id);
    }

    public Optional<Manager> getManagerIdByEmail(String email) {
        // Replace with actual repository call to fetch user ID by email
        return managerRepository.findManagerIdByEmail(email);
    }
}
