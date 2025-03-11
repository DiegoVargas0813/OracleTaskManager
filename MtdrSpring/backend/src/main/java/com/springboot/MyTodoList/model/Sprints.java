package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "SPRINTS")
public class Sprints {
    @Id
    @Column(name = "SPRINT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Column(name = "NAME")
    String name;
    @Column(name = "START_DATE")
    OffsetDateTime start_date;
    @Column(name = "END_DATE")
    OffsetDateTime end_date;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Projects project;
}
