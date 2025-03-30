package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;

import java.util.List;

@Entity
@Table(name = "TASKS")
public class Task {
    @Id
    @Column(name = "TASK_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "NAME")
    String name;
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "STATUS")
    String status;
    @Column(name = "STORY_POINTS")
    int storyPoints;
    @Column(name = "ESTIMATED_HOURS")
    int estimatedHours;
    @Column(name = "REAL_HOURS")
    int realHours;
    
    @ManyToOne
    @JoinColumn(name = "SPRINT_ID")
    @JsonBackReference
    private Sprint sprint;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Assignment> assignments = new ArrayList<>();

    public Task() {
    }

    public Task(int id, String name, String description, String status, int storyPoints, int estimatedHours , int realHours, Sprint sprint, List<Assignment> assignments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.storyPoints = storyPoints;
        this.estimatedHours = estimatedHours;
        this.realHours = realHours;
        this.sprint = sprint;
        this.assignments = assignments;
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

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public int getRealHours() {
        return realHours;
    }

    public void setRealHours(int realHours) {
        this.realHours = realHours;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
        assignment.setTask(this);
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", sprint=" + sprint +
                ", assignments=" + assignments +
                '}';
    }
}
