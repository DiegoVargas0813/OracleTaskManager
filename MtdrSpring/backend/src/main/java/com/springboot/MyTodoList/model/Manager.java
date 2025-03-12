package com.springboot.MyTodoList.model;

//https://tenmilesquare.com/resources/software-development/spring-boot-jpa-relationship-quick-guide/

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "MANAGERS")
public class Manager {
    @Id
    @Column(name = "MANAGER_ID")
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
    
    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Project> projects;

    public Manager() {
    }

    public Manager(int id, String name, String role, String email, String password, OffsetDateTime creation_ts, List<User> users, List<Project> projects) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.creation_ts = creation_ts;
        this.users = users;
        this.projects = projects;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", creation_ts=" + creation_ts +
                ", users=" + users +
                ", projects=" + projects +
                '}';
    }
}