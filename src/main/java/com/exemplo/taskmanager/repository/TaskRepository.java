package com.exemplo.taskmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.taskmanager.model.Project;
import com.exemplo.taskmanager.model.Task;
import com.exemplo.taskmanager.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);

    List<Task> findByProject(Project project);

    Optional<Task> findByIdAndUser(Long id, User user);

    List<Task> findByUserAndProjectIsNull(User user);
}