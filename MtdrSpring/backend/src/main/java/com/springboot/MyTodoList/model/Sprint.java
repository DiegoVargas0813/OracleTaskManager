package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.OffsetDateTime;

import java.util.List;

@Entity
@Table(name = "SPRINTS")
public class Sprint {
    @Id
    @Column(name = "SPRINT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "NAME")
    String name;
    @Column(name = "START_DATE")
    OffsetDateTime startDate;
    @Column(name = "END_DATE")
    OffsetDateTime endDate;
    
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    @JsonBackReference
    private Project project;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Task> tasks;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Issue> issues;

    public Sprint() {
    }

    public Sprint(int id, String name, OffsetDateTime startDate, OffsetDateTime endDate, Project project, List<Task> tasks, List<Issue> issues) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
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

    public OffsetDateTime getstartDate() {
        return startDate;
    }

    public void setstartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getendDate() {
        return endDate;
    }

    public void setendDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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
        return "Sprint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", project=" + project +
                ", tasks=" + tasks +
                ", issues=" + issues +
                '}';
    }
}
