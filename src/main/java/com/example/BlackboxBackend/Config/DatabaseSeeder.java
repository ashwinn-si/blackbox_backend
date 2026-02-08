package com.example.BlackboxBackend.Config;

import com.example.BlackboxBackend.DTO.RoleEnum;
import com.example.BlackboxBackend.Domain.Staff;
import com.example.BlackboxBackend.Repository.StaffRepository;
import com.example.BlackboxBackend.Utils.BcryptService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final StaffRepository staffRepository;
    private final BcryptService bcryptService;

    DatabaseSeeder(StaffRepository staffRepository, BcryptService bcryptService){
        this.staffRepository = staffRepository;
        this.bcryptService = bcryptService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==============CREATING DEFAULT ADMIN==============");
        Optional<Staff> staff = staffRepository.findByUsername("admin");

        if(staff.isPresent()){
            System.out.println("==============DEFAULT ADMIN ALREADY EXISTS==============");
            return;
        }

        String hashPassword = bcryptService.hashPassword("root");

        Staff defaultAdmin = new Staff("admin", hashPassword, "developer", true, RoleEnum.SUPER_ADMIN);

        staffRepository.save(defaultAdmin);

        System.out.println("=====================DEFAULT ADMIN CREATED===============");
    }
}
