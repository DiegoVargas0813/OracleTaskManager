package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;
import com.springboot.MyTodoList.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.MyTodoList.model.Manager;
import com.springboot.MyTodoList.model.UsersAndManagers;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity createUser(@RequestBody User user) throws Exception {
        User newUser = userService.createUser(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location",""+newUser.getId());
        responseHeaders.set("Access-Control-Expose-Headers","location");
        return ResponseEntity.ok()
            .headers(responseHeaders).build();
    }

    @GetMapping
        public UsersAndManagers getAllUsersAndManagers() {
        List<User> users = userService.getAllUsers();
        List<Manager> managers = managerService.getAllManagers();
        return new UsersAndManagers(users, managers);
    }


/* 
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id){
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/manager/{id}")
    public ResponseEntity<List<User>> getUsersByManagerId(@PathVariable int id) {
        List<User> users = userService.getUserByManagerId(id);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }
}
