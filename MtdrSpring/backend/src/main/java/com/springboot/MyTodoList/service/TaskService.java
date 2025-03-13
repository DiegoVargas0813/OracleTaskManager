package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.Assignment;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.AssignmentRepository;
import com.springboot.MyTodoList.repository.TaskRepository;
import com.springboot.MyTodoList.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional // Ensures atomicity: either all operations succeed or none.
    public Task createTask(int userId, Task task) {
        // 1. Find the User, or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Save the Task
        Task savedTask = taskRepository.save(task);

        // 3. Create a new Assignment entry linking the Task and User
        Assignment assignment = new Assignment();
        assignment.setTask(savedTask);
        assignment.setUser(user);
        assignment.setAssignment_date(OffsetDateTime.now());

        // 4. Maintain bidirectional relationships if needed
        savedTask.getAssignments().add(assignment); // If Task has an assignments list
        user.getAssignments().add(assignment); // If User has an assignments list

        // 5. Save the Assignment
        assignmentRepository.save(assignment);

        return savedTask;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(int id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByUserId(int userId) {
        return taskRepository.findAllTasksByUserId(userId);
    }
}
