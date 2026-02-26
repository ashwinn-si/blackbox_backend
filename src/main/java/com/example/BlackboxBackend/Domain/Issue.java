package com.example.BlackboxBackend.Domain;

import com.example.BlackboxBackend.DTO.IssueStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Issue {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resolved_id")
  private Staff staff = null;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Image> images = new ArrayList<>();

  private String audio = "";

  @Column(name = "resolution", nullable = true)
  private String resolution;

  @Enumerated(value = EnumType.STRING)
  private IssueStatusEnum status = IssueStatusEnum.CREATED;

  @Column(nullable = false)
  private Date createdAt;

  @Column
  private Date resolvedAt = null;
}
