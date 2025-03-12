package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity createProject(@RequestBody Project project) throws Exception {
        Project newProject = projectService.createProject(project);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location",""+newProject.getId());
        responseHeaders.set("Access-Control-Expose-Headers","location");
        return ResponseEntity.ok()
            .headers(responseHeaders).build();
    }

    @GetMapping
    public List<Project> getAllProjects(){
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable int id){
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
