package com.example.BlackboxBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtDTO {
    Long userId;
    RoleEnum role;
    boolean isSuperAdmin;
}
