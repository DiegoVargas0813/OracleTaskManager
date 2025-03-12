package com.springboot.MyTodoList.model;

import javax.persistence.*;
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
    boolean status;
    
    @ManyToOne
    @JoinColumn(name = "ASSIGNED_TO")
    private User assignedTo;
    
    @ManyToOne
    @JoinColumn(name = "SPRINT_ID")
    private Sprint sprint;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Assignment> assignments;
}
