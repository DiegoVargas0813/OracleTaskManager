package com.springboot.MyTodoList.model;

import javax.persistence.*;
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
    private Manager assignedTo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sprint> sprints;
}
