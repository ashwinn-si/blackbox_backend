package com.example.BlackboxBackend.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "issue_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "issue_id", nullable = false)
  private Issue issue;

  @Column(nullable = false)
  private String imagePath;
}
