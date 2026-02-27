package com.example.BlackboxBackend.Repository;

import com.example.BlackboxBackend.DTO.IssueStatusEnum;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    Page<Issue> findByDepartmentAndStatusOrderByCreatedAtDesc(Department department, IssueStatusEnum issueStatusEnum, PageRequest pageRequest);
}
