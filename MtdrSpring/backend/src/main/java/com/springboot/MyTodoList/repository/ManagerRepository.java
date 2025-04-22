package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface ManagerRepository extends JpaRepository<Manager,Integer> {


    @Query("SELECT m.id FROM Manager m WHERE m.email = :email")
    Integer findManagerIdByEmail(@Param("email") String email);

}
