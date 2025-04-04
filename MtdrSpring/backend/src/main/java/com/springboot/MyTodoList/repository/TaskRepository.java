package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@EnableTransactionManagement
public interface TaskRepository extends JpaRepository<Task,Integer> {
    @Query("SELECT t FROM Task t JOIN t.assignments a WHERE a.user.id = :userId")
    List<Task> findAllTasksByUserId(int userId);

    @Query("UPDATE Task t SET t.sprint.id = :sprintId WHERE t.id = :taskId")
    @Transactional
    @Modifying
    void assignTaskToSprint(int taskId, int sprintId);

    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :taskId")
    @Transactional
    @Modifying
    void updateTaskStatus(int taskId, String status);

    // Obtener tareas de un usuario en un sprint específico
    @Query("SELECT t FROM Task t JOIN t.assignments a WHERE a.user.id = :userId AND t.sprint.id = :sprintId")
    List<Task> findTasksByUserIdAndSprintId(int userId, int sprintId);

    @Query("UPDATE Task t SET t.realHours = :realHours WHERE t.id = :taskId")
    @Transactional
    @Modifying
    void updateTaskRealHours(int taskId, int realHours); // Actualizar horas reales de una tarea

    @Query("SELECT t FROM Task t JOIN t.assignments a WHERE t.sprint.id = :sprintId") //Quiero poder ver los detalles de las tareas de un sprint y que también me muestre el usuario asignado a la tarea
    List<Task> findTasksBySprintId(int sprintId);

}
