package com.exemplo.taskmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.taskmanager.model.Project;
import com.exemplo.taskmanager.model.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserAndParentIsNull(User user);

    List<Project> findByParent(Project parent);

    Optional<Project> findByIdAndUser(Long id, User user);
}