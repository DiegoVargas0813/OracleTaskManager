package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ASSIGNMENTS")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ASSIGNMENT_ID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "TASK_ID") 
    @JsonIgnore
    private Task task;
    
    @ManyToOne
    @JoinColumn(name = "USER_ID") // Explicitly specify foreign key column
    @JsonIgnore
    private User user;

    @Column(name = "ASSIGNMENT_DATE")
    private OffsetDateTime assignmentDate;

    public Assignment() {
    }

    public Assignment(Task task, User user, OffsetDateTime assignmentDate) {
        this.task = task;
        this.user = user;
        this.assignmentDate = assignmentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OffsetDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(OffsetDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", task=" + task +
                ", user=" + user +
                ", assignmentDate=" + assignmentDate +
                '}';
    }
}
