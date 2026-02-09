package com.example.BlackboxBackend.Controller;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.Service.DepartmentService;
import com.example.BlackboxBackend.Utils.ResponseHandler;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
class AddDepartmentDTO{
    @Size(min = 1)
    private String departmentName;
}

@RestController
@RequestMapping("/api/department")
@Validated
public class DepartmentController {
    private final DepartmentService departmentService;

    DepartmentController(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    @PostMapping("/admin/add")
    public ResponseEntity<?> addDepartment(@RequestBody @Valid AddDepartmentDTO addDepartmentDTO) throws CustomError {
        departmentService.addDepartment(addDepartmentDTO.getDepartmentName());
        return ResponseHandler.handleResponse(HttpStatus.OK,
                null,
                "Department Created");
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable @NotNull(message = "Id is required") Long id,
                                              @RequestBody @Valid AddDepartmentDTO addDepartmentDTO) throws CustomError {
        departmentService.updateDepartment(id, addDepartmentDTO.getDepartmentName());
        return ResponseHandler.handleResponse(HttpStatus.OK,
                null,
                "Department Updated");
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable @NotNull(message = "Id is required") Long id) throws CustomError {
        departmentService.deleteDepartment(id);
        return ResponseHandler.handleResponse(HttpStatus.OK,
                null,
                "Department Deleted");
    }

    @GetMapping("/admin/get-all")
    public ResponseEntity<?> getAllDepartment(@RequestParam @NotNull(message = "Page is Required") @Min(value = 1, message = "Page is min of 1") Integer page,
                                              @RequestParam @NotNull(message = "Size is Required") @Min(value = 1, message = "Size is min of 1") Integer size,
                                              @RequestParam(required = false) @Size(min = 0, message = "Department Name is Required") String departmentName){
        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                departmentService.getAllDepartment(page, size, departmentName),
                "All Departments"
        );
    }

    @GetMapping("/admin/get/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable @NotNull(message = "Id is required") Long id) throws CustomError {
        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                departmentService.getDepartment(id),
                "Department Details"
        );
    }
}
