package com.example.BlackboxBackend.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
  private List<Issue> issueList = new ArrayList<>();

  @ManyToMany(mappedBy = "permittedDepartments", fetch = FetchType.LAZY)
  private List<Staff> staffMembers = new ArrayList<>();
}
