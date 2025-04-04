package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.Assignment;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.AssignmentRepository;
import com.springboot.MyTodoList.repository.TaskRepository;
import com.springboot.MyTodoList.repository.UserRepository;
import com.springboot.MyTodoList.repository.SprintRepository;
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

    @Autowired
    private SprintRepository sprintRepository;

    @Transactional // Ensures atomicity: either all operations succeed or none.
    public Task createTask(int userId, Task task) {
        // 1. Find the User, or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // 2. Handle sprint assignment - if no sprint is set, use sprint_id=21 as fallback
        if (task.getSprint() == null) {
            Sprint defaultSprint = sprintRepository.findById(21)
                    .orElseThrow(() -> new RuntimeException("Default sprint not found with ID: 21"));
            task.setSprint(defaultSprint);
        }

        // 3. Save the Task
        Task savedTask = taskRepository.save(task);

        // 4. Create a new Assignment entry linking the Task and User
        Assignment assignment = new Assignment();
        assignment.setTask(savedTask);
        assignment.setUser(user);
        assignment.setAssignmentDate(OffsetDateTime.now());

        // 5. Maintain bidirectional relationships if needed
        savedTask.getAssignments().add(assignment); // If Task has an assignments list
        user.getAssignments().add(assignment); // If User has an assignments list

        // 6. Save the Assignment
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

    public List<Task> getTasksByUserIdAndSprintId(int userId, int sprintId) {
        return taskRepository.findTasksByUserIdAndSprintId(userId, sprintId);
    }

    public void assignTaskToSprint(int taskId, int sprintId) {
        taskRepository.assignTaskToSprint(taskId, sprintId);
    }

    public void putTaskStatus(int taskId, String status) {
        taskRepository.updateTaskStatus(taskId, status);
    }

    public void putTaskRealHours(int taskId, int realHours) {
        taskRepository.updateTaskRealHours(taskId, realHours);
    }

    public List<Task> getTasksBySprintId(int sprintId) {
        return taskRepository.findTasksBySprintId(sprintId);
    }

}
