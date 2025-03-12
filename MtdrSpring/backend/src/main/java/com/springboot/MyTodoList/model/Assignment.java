package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ASSIGNMENTS")
public class Assignment {
    @Id
    @Column(name = "ASSIGNMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private Task task;
    
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "ASSIGNMENT_DATE")
    OffsetDateTime assignment_date;
}
