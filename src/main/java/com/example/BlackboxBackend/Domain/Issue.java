package com.example.BlackboxBackend.Domain;

import com.example.BlackboxBackend.DTO.IssueStatusEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.sql.Date;
import java.util.List;


@Entity(name = "issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_id")
    private Staff staff;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "issue_text", nullable = false)
    private JsonNode issueText;

    //keep as 0 and we dont need it
    @Column(nullable = false)
    private List<String> images;

    @Column(name = "issue_content", nullable = false)
    private String issueContent;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "resolution_text", nullable = true)
    private JsonNode resolutionText;

    @Column(name = "resolution_content", nullable = true)
    private String resolutionContent;

    @Enumerated(value = EnumType.STRING)
    private IssueStatusEnum status;

    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = true)
    private Date resolvedAt;
}
