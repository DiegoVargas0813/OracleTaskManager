package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ISSUES")
public class Issue {
    @Id
    @Column(name = "ISSUE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    
    @Column(name = "NAME")
    String name;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "STATUS")
    boolean status;

    @Column(name = "CREATION_TS")
    OffsetDateTime creation_ts;

    @ManyToOne
    @JoinColumn(name = "ASSIGNED_TO")
    private User assignedTo;
    
    @ManyToOne
    @JoinColumn(name = "SPRINT_ID")
    private Sprint sprint;

    public Issue() {
    }

    public Issue(int id, String name, String description, boolean status, OffsetDateTime creation_ts, User assignedTo, Sprint sprint) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.creation_ts = creation_ts;
        this.assignedTo = assignedTo;
        this.sprint = sprint;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public OffsetDateTime getCreation_ts() {
        return creation_ts;
    }

    public void setCreation_ts(OffsetDateTime creation_ts) {
        this.creation_ts = creation_ts;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", creation_ts=" + creation_ts +
                ", assignedTo=" + assignedTo +
                ", sprint=" + sprint +
                '}';
    }

}
