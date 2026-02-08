package com.example.BlackboxBackend.Repository;

import com.example.BlackboxBackend.Domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    public Optional<Department> findByNameAndIsDeletedFalse(String name);

    public Page<Department> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, PageRequest pageable);

    public Page<Department> findByIsDeletedFalse(PageRequest pageRequest);

    public Optional<Department> findByIdAndIsDeletedFalse(Long id);
}
