package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;

import java.util.List;

public class KPIService {
    private final TaskService taskService;
    private final UserService userService;
    private final SprintService sprintService;

    public KPIService(TaskService taskService, UserService userService, SprintService sprintService) {
        this.taskService = taskService;
        this.userService = userService;
        this.sprintService = sprintService;
    }

    public String calculateKPIsForUser(int userId) {
        Sprint currentSprint = sprintService.getActiveSprintsByUserId(userId).get(0);
        int sprintId = currentSprint.getId();
        List<Task> tasks = taskService.getTasksByUserIdAndSprintId(userId, sprintId);



        int totalTasks = tasks.size();
        long notStartedTasks = tasks.stream().filter(task -> "Not-started".equals(task.getStatus())).count();
        long inProgressTasks = tasks.stream().filter(task -> "Started".equals(task.getStatus())).count();
        long completedTasks = tasks.stream().filter(task -> "Complete".equals(task.getStatus())).count();

        double totalEstimatedHours = tasks.stream()
            .filter(task -> "Complete".equals(task.getStatus()))
            .mapToDouble(Task::getEstimatedHours)
            .sum();

        double totalRealHours = tasks.stream()
            .filter(task -> "Complete".equals(task.getStatus()))
            .mapToDouble(Task::getRealHours)
            .sum();

        double taskCompletionRatio = (totalTasks > 0) ? (completedTasks / (double) totalTasks) * 100 : 0;

        double efficiency = (totalRealHours > 0) ? (totalEstimatedHours/ totalRealHours) * 100 : 0;

        return String.format(
            "KPIs for User #%d:\n" +
            "- Total Tasks: %d\n" +
            "- Not Started: %d\n" +
            "- In Progress: %d\n" +
            "- Completed: %d\n" +
            "- Task Completion Ratio: %.2f%%\n" +
            "- Efficiency (Real vs Estimated Hours): %.2f%%",
            userId, totalTasks, notStartedTasks, inProgressTasks, completedTasks,
            taskCompletionRatio, efficiency
        );
    }
}
