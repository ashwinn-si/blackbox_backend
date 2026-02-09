package com.example.BlackboxBackend.Repository;

import com.example.BlackboxBackend.Domain.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUsername(String username);

    Page<Staff> findDistinctByPermittedDepartments_IdIn(List<Long> ids, Pageable pageable);

}
