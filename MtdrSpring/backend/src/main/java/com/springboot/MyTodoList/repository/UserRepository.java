package com.springboot.MyTodoList.repository;

import java.util.Optional;
import com.springboot.MyTodoList.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@EnableTransactionManagement
public interface UserRepository extends JpaRepository<User,Integer> {
    List<User> findUserByManagerId(int id);
    
    //Comment

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Integer findUserIdByEmail(@Param("email") String email);

    @Query("SELECT u.id FROM User u WHERE u.manager.id = :managerId")
    List<Integer> findUserIdsByManagerId(@Param("managerId") int managerId);

    Optional<User> findByEmail(String email);
}
