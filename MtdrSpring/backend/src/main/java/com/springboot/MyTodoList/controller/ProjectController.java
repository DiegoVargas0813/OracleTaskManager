package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.net.URI;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) throws Exception {
        try {
            Project newProject = projectService.createProject(project);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location",""+newProject.getId());
            responseHeaders.set("Access-Control-Expose-Headers","location");
            return ResponseEntity.created(URI.create("/api/projects/" + newProject.getId()))
                .headers(responseHeaders)
                .body(newProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
       
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(){
        List<Project> projects = projectService.getAllProjects();
        if (projects == null || projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable int id){
        Optional<Project> project = projectService.getProjectById(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/manager/{id}")
    public ResponseEntity<List<Project>> getProjectsByManagerId(@PathVariable int id){
        List<Project> projects = projectService.getProjectsByManagerId(id);
        if (projects == null || projects.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(projects);
    }
}
