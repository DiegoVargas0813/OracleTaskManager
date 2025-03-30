package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
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
    OffsetDateTime creationTs;
    
    @OneToOne
    @JoinColumn(name = "MANAGER_ID")
    @JsonBackReference
    private Manager manager;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Sprint> sprints;

    public Project() {
    }

    public Project(int id, String name, String description, OffsetDateTime creationTs, Manager manager, List<Sprint> sprints) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationTs = creationTs;
        this.manager = manager;
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

    public OffsetDateTime getCreationTs() {
        return creationTs;
    }

    public void setCreationTs(OffsetDateTime creationTs) {
        this.creationTs = creationTs;
    }

    public Manager getmanager() {
        return manager;
    }

    public void setmanager(Manager manager) {
        this.manager = manager;
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
                ", creationTs=" + creationTs +
                ", manager=" + manager +
                ", sprints=" + sprints +
                '}';
    }
}
