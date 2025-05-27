package com.springboot.MyTodoList.model;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User implements UserDetails{
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
    OffsetDateTime creationTs;
        
    @ManyToOne
    @JoinColumn(name = "MANAGER_ID")
    @JsonBackReference
    private Manager manager;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Issue> issues;

    public User() {
    }

    public User(int id, String name, String role, String email, String password, OffsetDateTime creationTs, Manager manager, List<Assignment> assignments, List<Issue> issues) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
        this.creationTs = creationTs;
        this.manager = manager;
        this.assignments = assignments;
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

    public OffsetDateTime getCreationTs() {
        return creationTs;
    }

    public void setCreationTs(OffsetDateTime creationTs) {
        this.creationTs = creationTs;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
        assignment.setUser(this);
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

     @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + this.role.toUpperCase()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
public String getUsername() {
    return this.email; // o name si prefieres
}



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", creationTs=" + creationTs +
                ", manager=" + manager +
                ", assignments=" + assignments +
                ", issues=" + issues +
                '}';
    }


}

