package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface ManagerRepository extends JpaRepository<Manager,Integer> {


    @Query("SELECT m FROM Manager m WHERE m.email = :email")
    Optional<Manager> findManagerIdByEmail(@Param("email") String email);

}
