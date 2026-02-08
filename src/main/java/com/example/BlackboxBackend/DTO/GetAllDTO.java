package com.example.BlackboxBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllDTO <T>{
    T data;
    Integer currPage;
    Integer size;
    Integer totalPages;
}
