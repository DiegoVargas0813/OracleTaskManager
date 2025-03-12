package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {
    @Autowired
    private SprintService sprintService;

    @PostMapping
    public ResponseEntity createSprint(@RequestBody Sprint sprint) throws Exception {
        Sprint newSprint = sprintService.createSprint(sprint);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location",""+newSprint.getId());
        responseHeaders.set("Access-Control-Expose-Headers","location");
        return ResponseEntity.ok()
            .headers(responseHeaders).build();
    }

    @GetMapping
    public List<Sprint> getAllSprints(){
        return sprintService.getAllSprints();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable int id){
        Optional<Sprint> sprint = sprintService.getSprintById(id);
        return sprint.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
