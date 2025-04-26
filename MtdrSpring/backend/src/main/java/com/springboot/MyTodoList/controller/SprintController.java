package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.net.URI;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {
    @Autowired
    private SprintService sprintService;

    @PostMapping
    public ResponseEntity createSprint(@RequestBody Sprint sprint) throws Exception {
        try{
            Sprint newSprint = sprintService.createSprint(sprint);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location",""+newSprint.getId());
            responseHeaders.set("Access-Control-Expose-Headers","location");
            return ResponseEntity.created(URI.create("/api/sprints/" + newSprint.getId()))
                    .headers(responseHeaders)
                    .body(newSprint);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public List<Sprint> getAllSprints(){
        return sprintService.getAllSprints();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable int id){
        Optional<Sprint> sprint = sprintService.getSprintById(id);
        if (sprint == null) {
            return ResponseEntity.notFound().build();
        }
        return sprint.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Sprint>> getActiveSprints() {
        List<Sprint> activeSprints = sprintService.getActiveSprints();
        if (activeSprints == null || activeSprints.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(activeSprints);
    }

    @GetMapping("/manager/{id}")
    public List<Sprint> getSprintsByManagerId(@PathVariable int id) {
        return sprintService.getSprintsByManagerId(id);
    }

    @GetMapping("/user/{id}")
    public List<Sprint> getSprintsByUserId(@PathVariable int id) {
        return sprintService.getSprintsByUserId(id);
    }

    @GetMapping("/active/user/{id}")
    public List<Sprint> getActiveSprintsByUserId(@PathVariable int id) {
        return sprintService.getActiveSprintsByUserId(id);
    }

    @GetMapping("/active/manager/{id}")
    public List<Sprint> getActiveSprintsByManagerId(@PathVariable int id) {
        return sprintService.getActiveSprintsByManagerId(id);
    }
}
