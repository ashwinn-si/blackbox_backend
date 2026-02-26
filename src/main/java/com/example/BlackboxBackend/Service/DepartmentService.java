package com.example.BlackboxBackend.Service;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.DTO.GetAllDTO;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
class DepartmentDTO {
    String departmentName;
    Long id;
    Long noStaff;
    Long issuesCount;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class DropdownDTO{
    private Long key;
    private String value;
}

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;


    DepartmentService(DepartmentRepository departmentRepository){
        this.departmentRepository = departmentRepository;
    }

    public void addDepartment(String departmentName) throws CustomError{
        if (departmentRepository.findByNameAndIsDeletedFalse(departmentName).isPresent()) {
            throw new CustomError(HttpStatus.CONFLICT, "Department Already Exists");
        }

        Department department = new Department(departmentName);
        departmentRepository.save(department);
    }

    public void deleteDepartment(Long departmentId) throws CustomError{
        Department department = isDepartmentExists(departmentId);

        department.setIsDeleted(true);

        departmentRepository.save(department);
    }

    public void updateDepartment(Long departmentId, String departmentName) throws CustomError{
        Department department = isDepartmentExists(departmentId);
        department.setName(departmentName);
        departmentRepository.save(department);
    }

    public GetAllDTO getAllDepartment(Integer page, Integer size, String departmentName){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Department> data;
        if (departmentName != null){
            data = departmentRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(departmentName, pageRequest);
        }else {
            data = departmentRepository.findByIsDeletedFalse(pageRequest);
        }
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        for(Department department: data.getContent()){
            departmentDTOList.add(new
                    DepartmentDTO(department.getName(),
                    department.getId(),
                    department.getStaffMembers().stream().count(), department.getIssueList().stream().count()));
        }
        return new GetAllDTO(departmentDTOList, page, size, data.getTotalPages());
    }

    public List<DropdownDTO> getDeparmentDropDown(){
        List<Department> departments = departmentRepository.findByIsDeletedFalse();
        List<DropdownDTO> departmentList = new ArrayList<>();
        for(Department department: departments){
            departmentList.add(new DropdownDTO(department.getId(), department.getName()));
        }
        return departmentList;
    }

    public DepartmentDTO getDepartment(Long departmentId) throws CustomError{
        Department department = isDepartmentExists(departmentId);

        return new
                DepartmentDTO(department.getName(),
                department.getId(),
                department.getStaffMembers().stream().count(), department.getIssueList().stream().count());
    }

    public Department isDepartmentExists(Long departmentId) throws CustomError{
        return departmentRepository.findByIdAndIsDeletedFalse(departmentId).orElseThrow(() ->
            new CustomError(HttpStatus.NOT_FOUND, "Department not found")
        );
    }
}
