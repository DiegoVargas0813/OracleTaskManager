package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.OffsetDateTime;

import java.util.List;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "NAME")
    String name;
    @Column(name = "ROLE")
    String role;
    @Column(name = "EMAIL", unique = true)
    String email;
    @Column(name = "PASSWORD")
    String password;
    @Column(name = "CREATION_TS")
    OffsetDateTime creation_ts;
    
    @ManyToOne
    @JoinColumn(name = "MANAGER_ID")
    @JsonBackReference
    private Manager manager;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Issue> issues;

    public User() {
    }

    public User(int id, String name, String role, String email, String password, OffsetDateTime creation_ts, Manager manager, List<Task> tasks, List<Issue> issues) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.creation_ts = creation_ts;
        this.manager = manager;
        this.tasks = tasks;
        this.issues = issues;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public OffsetDateTime getCreation_ts() {
        return creation_ts;
    }

    public void setCreation_ts(OffsetDateTime creation_ts) {
        this.creation_ts = creation_ts;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", creation_ts=" + creation_ts +
                ", manager=" + manager +
                ", tasks=" + tasks +
                ", issues=" + issues +
                '}';
    }
}
