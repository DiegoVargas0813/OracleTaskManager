package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Column(name = "NAME")
    String name;
    @Column(name = "ROLE")
    String role;
    @Column(name = "EMAIL", unique = true)
    String email;
    @Column(name = "PASSWORD")
    String password;
    @Column(name = "CREATION_TS")
    OffsetDateTime creation_ts;
    @ManyToOne
    @JoinColumn(name = "MANAGER_ID")
    private Manager manager;
}
