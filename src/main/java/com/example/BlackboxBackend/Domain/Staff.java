package com.example.BlackboxBackend.Domain;

import com.example.BlackboxBackend.DTO.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "staffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String message;

    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "true")
    private boolean isSuperAdmin;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;

    @Column(nullable = false)
    private List<Department> permittedDepartment = new ArrayList<>();
}
