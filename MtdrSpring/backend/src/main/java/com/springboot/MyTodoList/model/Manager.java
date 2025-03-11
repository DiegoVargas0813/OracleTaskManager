package com.springboot.MyTodoList.model;

//https://tenmilesquare.com/resources/software-development/spring-boot-jpa-relationship-quick-guide/

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MANAGERS")
public class Manager {
    @Id
    @Column(name = "MANAGER_ID")
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
    @OneToMany(mappedBy = "manager")
    private Set<User> users = new HashSet<>();
}