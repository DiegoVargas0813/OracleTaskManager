package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {
    @Autowired
    private ManagerRepository managerRepository;

    public Manager createManager(Manager manager){
        return managerRepository.save(manager);
    }

    public Manager getManagerById(int id){
        return managerRepository.findById(id).orElse(null);
    }
}
