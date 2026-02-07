package com.example.BlackboxBackend.Service;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.Domain.Staff;
import com.example.BlackboxBackend.Repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaffService {
    private final StaffRepository staffRepository;

    StaffService(StaffRepository staffRepository){
        this.staffRepository = staffRepository;
    }

    public Staff isStaffExists(String username) throws CustomError {
        return staffRepository.findByUsername(username)
                .orElseThrow(() -> new CustomError(
                        HttpStatus.NOT_FOUND,
                        "Staff Not Found"
                ));
    }

}
