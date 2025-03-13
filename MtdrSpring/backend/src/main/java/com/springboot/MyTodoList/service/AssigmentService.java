package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Assignment;
import com.springboot.MyTodoList.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssigmentService {
    @Autowired
    private AssignmentRepository assignmentRepository;

    public Assignment createAssignment(Assignment assignment){
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAllAssignments(){
        return assignmentRepository.findAll();
    }

    public Optional<Assignment> getAssignmentById(int id){
        return assignmentRepository.findById(id);
    }
}
