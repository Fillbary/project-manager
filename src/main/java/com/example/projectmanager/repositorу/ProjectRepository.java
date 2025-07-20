package com.example.projectmanager.repositorу;

import com.example.projectmanager.entity.Project;
import com.example.projectmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOwner(User owner);

    Optional<Project> findByIdAndOwner(Long id, User owner);

    Optional<Project> findById(Long id);

    boolean existsByNameAndOwner_Id(String name, Long ownerId);

    boolean existsById(Long id);

}
