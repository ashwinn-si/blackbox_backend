package com.example.BlackboxBackend.Service;

import com.example.BlackboxBackend.Constants.Constant;
import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.DTO.RoleEnum;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Domain.Staff;
import com.example.BlackboxBackend.Repository.DepartmentRepository;
import com.example.BlackboxBackend.Repository.StaffRepository;
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
class DepartmentDropDownDTO{
    String value;
    Long key;
}
@Service
public class PermissionService {
    StaffRepository staffRepository;
    DepartmentRepository departmentRepository;

    PermissionService(StaffRepository staffRepository, DepartmentRepository departmentRepository){
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
    }

    public boolean checkPermissionPage(Long userId, String permission) throws CustomError {

        Staff staff = staffRepository.findById(userId).orElseThrow(() -> {
            return new CustomError(HttpStatus.UNAUTHORIZED, "STAFF NOT FOUND");
        });

        RoleEnum role = staff.getRole();

        List<String> allowedScreens = Constant.getPermissionList(role);

        if(!allowedScreens.contains("/"+permission)){
            return false;
        }
        return true;
    }

    public boolean checkPermissionDepartment(Long userId, Long departmentId) throws CustomError {
        Staff staff = staffRepository.findById(userId).orElseThrow(() -> {
            return new CustomError(HttpStatus.UNAUTHORIZED, "STAFF NOT FOUND");
        });

        if(staff.isSuperAdmin()){
            return true;
        }

        List<Department> role = staff.getPermittedDepartments();

        for(Department d: role){
            if(d.getId() == departmentId){
                return true;
            }
        }

        return false;
    }

    public List<DepartmentDropDownDTO> getPermittedDepartmented(Long userId) throws CustomError{
        Staff staff = staffRepository.findById(userId).orElseThrow(() -> {
            return new CustomError(HttpStatus.UNAUTHORIZED, "STAFF NOT FOUND");
        });
        List<DepartmentDropDownDTO> departmentsList = new ArrayList<>();
        List<Department> permittedDepartment;
        if(staff.isSuperAdmin()){
            permittedDepartment = departmentRepository.findByIsDeletedFalse();
        }else{
            permittedDepartment = staff.getPermittedDepartments();
        }

        for(Department departments: permittedDepartment){
            departmentsList.add(new DepartmentDropDownDTO( departments.getName(), departments.getId()));
        }

        return departmentsList;
    }
}
