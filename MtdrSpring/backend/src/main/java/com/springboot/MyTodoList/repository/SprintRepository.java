package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.OffsetDateTime;
import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface SprintRepository extends JpaRepository<Sprint,Integer> {
    List<Sprint> findByStartDateBeforeAndEndDateAfter(OffsetDateTime now1, OffsetDateTime now2);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = (SELECT u.manager.projects.id FROM User u WHERE u.id = :userId) AND s.startDate <= :now AND s.endDate >= :now")
    List<Sprint> findActiveSprintsByUserId(@Param("userId") int userId, @Param("now") OffsetDateTime now);

    @Query("SELECT s FROM Sprint s WHERE s.project.manager.id = :managerId AND s.startDate <= :now AND s.endDate >= :now")
    List<Sprint> findActiveSprintsByManagerId(@Param("managerId") int managerId, @Param("now") OffsetDateTime now);
}
