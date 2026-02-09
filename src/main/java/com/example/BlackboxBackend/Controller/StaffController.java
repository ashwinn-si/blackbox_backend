package com.example.BlackboxBackend.Controller;


import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.DTO.JwtDTO;
import com.example.BlackboxBackend.DTO.RoleEnum;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Service.StaffService;
import com.example.BlackboxBackend.Utils.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class AddStaffDTO{
    @NotNull
    @Size(min=1, message = "Username is Required")
    private String username;

    @NotNull
    @Size(min= 1, message = "Password is Required")
    private String password;

    @NotNull
    @Size(min= 1, message = "Name is Required")
    private String name;

    @NotNull(message = "Role is Required")
    private RoleEnum role;

    @NotNull(message = "IsSuperAdmin is Required")
    private Boolean isSuperAdmin;

    @NotNull
    @Size(min = 1, message = "Atleast One Department Is Needed")
    private List<Long> departmentAssignTo;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ChangePasswordDTO{
    @NotNull
    @Size(min= 1, message = "Password is Required")
    private String password;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UpdateStaffDTO{
    @NotNull
    @Size(min = 1, message = "Atleast One Department Is Needed")
    private List<Long> departmentAssignTo;
}



@RestController
@RequestMapping("/api/staff")
@Validated
public class StaffController {

    private final StaffService staffService;

    StaffController(StaffService staffService){
        this.staffService = staffService;
    }

    @PostMapping("/admin/add")
    public ResponseEntity<?> addStaff(@AuthenticationPrincipal JwtDTO jwtDTO, @RequestBody @Valid AddStaffDTO addStaffDTO) throws CustomError {
        //checking if the user can create a staff in the give departments
        List<Department> permittedDepartments = staffService.canCreateStaff(jwtDTO.getUserId(), addStaffDTO.getDepartmentAssignTo(), addStaffDTO.getIsSuperAdmin());

        staffService.addStaff(addStaffDTO.getUsername(), addStaffDTO.getPassword(), addStaffDTO.getName(), addStaffDTO.getRole(), addStaffDTO.getIsSuperAdmin(), permittedDepartments);

        return ResponseHandler.handleResponse(HttpStatus.CREATED, null, "Staff Created");
    }

    @PutMapping("/admin/update-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable @NotNull(message = "StaffId is required") Long id, @RequestBody @Valid ChangePasswordDTO changePasswordDTO) throws  CustomError{
        staffService.changePassword(id, changePasswordDTO.getPassword());

        return ResponseHandler.handleResponse(HttpStatus.OK, null, "Password Changed Successfully");
    }

    @PutMapping("/admin/update-department/{id}")
    public ResponseEntity<?> updateDepartment(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable @NotNull(message = "StaffId is required") Long id,
                                              @RequestBody @Valid UpdateStaffDTO updateStaffDTO) throws CustomError{
        List<Department> permittedDepartments = staffService.canCreateStaff(jwtDTO.getUserId(), updateStaffDTO.getDepartmentAssignTo(), false);

        staffService.updateDepartment(id, permittedDepartments);

        return ResponseHandler.handleResponse(HttpStatus.OK, null, "Departments Updated Successfully");
    }

    @GetMapping("/admin/get/{id}")
    public ResponseEntity<?> getStaff(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable @NotNull(message = "StaffId is required") Long id) throws CustomError{
        staffService.isAllowedToViewStaff(jwtDTO.getUserId(), id);

        return ResponseHandler.handleResponse(HttpStatus.OK, staffService.getStaff(id), "Staff Details");
    }

    @GetMapping("/admin/get-all")
    public ResponseEntity<?> getAllDepartment(@AuthenticationPrincipal JwtDTO jwtDTO,
                                              @RequestParam @NotNull(message = "Page is Required") @Min(value = 1, message = "Page is min of 1") Integer page,
                                              @RequestParam @NotNull(message = "Size is Required") @Min(value = 1, message = "Size is min of 1") Integer size,
                                              @RequestParam(required = false) @Size(min =1, message ="Atleast One Department Needed" ) List<Long> departmentIdList) throws CustomError {
        if(departmentIdList != null && !departmentIdList.isEmpty()){
            if(!staffService.isAllowedToViewStaff(jwtDTO.getUserId(), departmentIdList)){
                return ResponseHandler.handleResponse(HttpStatus.CONFLICT,
                        null,
                        "NOT PERMITTED");
            }
        }
        return ResponseHandler.handleResponse(HttpStatus.OK, staffService.getAllStaff(page, size, departmentIdList), "All Staff Details");
    }



}
