package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;

import java.util.List;

public class KPIService {
    private final TaskService taskService;
    private final UserService userService;

    public KPIService(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    public String calculateKPIsForUser(int userId) {
        List<Task> tasks = taskService.getTasksByUserId(userId);

        int totalTasks = tasks.size();
        long notStartedTasks = tasks.stream().filter(task -> "Not-started".equals(task.getStatus())).count();
        long inProgressTasks = tasks.stream().filter(task -> "Started".equals(task.getStatus())).count();
        long completedTasks = tasks.stream().filter(task -> "Complete".equals(task.getStatus())).count();

        return String.format(
            "KPIs for User #%d:\n- Total Tasks: %d\n- Not Started: %d\n- Started Tasks: %d\n- Completed Tasks: %d",
            userId, totalTasks, notStartedTasks, inProgressTasks, completedTasks
        );
    }

}
