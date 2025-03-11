package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ASSIGNMENTS")
public class Assignments {
    @Id
    @Column(name = "ASSIGNMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
