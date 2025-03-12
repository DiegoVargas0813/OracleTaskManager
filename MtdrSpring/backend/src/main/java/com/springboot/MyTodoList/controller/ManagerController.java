package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @PostMapping
    public ResponseEntity createManager(@RequestBody Manager manager) throws Exception {
        Manager newManager = managerService.createManager(manager);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location",""+newManager.getId());
        responseHeaders.set("Access-Control-Expose-Headers","location");
        return ResponseEntity.ok()
            .headers(responseHeaders).build();
    }

    @GetMapping
    public List<Manager> getAllManagers(){
        return managerService.getAllManagers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Manager> getManagerById(@PathVariable int id){
        Optional<Manager> manager = managerService.getManagerById(id);
        return manager.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
