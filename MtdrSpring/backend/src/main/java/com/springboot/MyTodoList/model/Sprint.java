package com.springboot.MyTodoList.model;

import javax.persistence.*;
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
    OffsetDateTime start_date;
    @Column(name = "END_DATE")
    OffsetDateTime end_date;
    
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Issue> issues;
}
