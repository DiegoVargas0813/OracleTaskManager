package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.OffsetDateTime;

import java.util.List;

@Entity
@Table(name = "PROJECTS")
public class Project {
    @Id
    @Column(name = "PROJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "NAME")
    String name;
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "CREATION_TS")
    OffsetDateTime creation_ts;
    
    @ManyToOne
    @JoinColumn(name = "ASSIGNED_TO")
    @JsonIgnore
    private Manager assignedTo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Sprint> sprints;

    public Project() {
    }

    public Project(int id, String name, String description, OffsetDateTime creation_ts, Manager assignedTo, List<Sprint> sprints) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creation_ts = creation_ts;
        this.assignedTo = assignedTo;
        this.sprints = sprints;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreation_ts() {
        return creation_ts;
    }

    public void setCreation_ts(OffsetDateTime creation_ts) {
        this.creation_ts = creation_ts;
    }

    public Manager getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Manager assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    @Override  
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creation_ts=" + creation_ts +
                ", assignedTo=" + assignedTo +
                ", sprints=" + sprints +
                '}';
    }
}
