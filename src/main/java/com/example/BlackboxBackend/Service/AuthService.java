package com.example.BlackboxBackend.Service;


import com.example.BlackboxBackend.Constants.Constant;
import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Domain.Staff;
import com.example.BlackboxBackend.Repository.DepartmentRepository;
import com.example.BlackboxBackend.Repository.StaffRepository;
import com.example.BlackboxBackend.Utils.BcryptService;
import com.example.BlackboxBackend.Utils.JwtService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
class StaffDTO{
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Departments{
        private Long key;
        private String value;
    }
    private String username;
    private String name;
    private String token;
    private List<Departments> allowedDepartments;
    private List<String> screens;
}

@Service
public class AuthService {
    private final StaffService staffService;
    private final BcryptService bcryptService;
    private final JwtService jwtService;
    private final DepartmentRepository departmentRepository;

    AuthService(StaffService staffService, BcryptService bcryptService, JwtService jwtService, DepartmentService departmentService, DepartmentRepository departmentRepository){
        this.staffService = staffService;
        this.jwtService = jwtService;
        this.bcryptService = bcryptService;
        this.departmentRepository = departmentRepository;
    }


    public StaffDTO login(String username, String password) throws CustomError {
        Staff staff = staffService.isStaffExists(username);
        boolean passwordResult = bcryptService.comparePassword(staff.getPassword(), password);

        if(!passwordResult){
            throw new CustomError(HttpStatus.CONFLICT, "Password Incorrect");
        }

        String token = jwtService.generateToken(staff.getId(), staff.isSuperAdmin());

        List<StaffDTO.Departments> departmentsList = new ArrayList<>();
        List<Department> permittedDepartment;
        if(staff.isSuperAdmin()){
            permittedDepartment = departmentRepository.findByIsDeletedFalse();
        }else{
            permittedDepartment = staff.getPermittedDepartments();
        }

        for(Department departments: permittedDepartment){
            departmentsList.add(new StaffDTO.Departments(departments.getId(), departments.getName()));
        }

        return new StaffDTO(staff.getUsername(), staff.getName(), token, departmentsList,Constant.permissionScreen.get(staff.getRole().toString()));
    }

}
