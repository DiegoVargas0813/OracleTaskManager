package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ISSUES")
public class Issue {
    @Id
    @Column(name = "ISSUE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    
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

}
