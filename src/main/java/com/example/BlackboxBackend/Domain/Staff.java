package com.example.BlackboxBackend.Domain;

import com.example.BlackboxBackend.DTO.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private boolean isSuperAdmin = false;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private RoleEnum role;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "staff_department_mapping", joinColumns = @JoinColumn(name = "staff_id"), inverseJoinColumns = @JoinColumn(name = "department_id"))
  private List<Department> permittedDepartments = new ArrayList<>();

  @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY)
  private List<Issue> resolvedIssues = new ArrayList<>();

}
