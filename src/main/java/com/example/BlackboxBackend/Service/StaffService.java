package com.example.BlackboxBackend.Service;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.DTO.GetAllDTO;
import com.example.BlackboxBackend.DTO.RoleEnum;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Domain.Staff;
import com.example.BlackboxBackend.Repository.DepartmentRepository;
import com.example.BlackboxBackend.Repository.StaffRepository;
import com.example.BlackboxBackend.Utils.BcryptService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Data
@AllArgsConstructor
@NoArgsConstructor
class GetStaffDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class DepartmentDTO{
        private Long key;
        private String value;
    }
    private Long id;
    private String username;
    private String name;
    private List<DepartmentDTO> allowedDepartments;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GetAllStaffDTO{
    private Long id;
    private String username;
    private String name;
    private Integer noSolvedIssues;
    private Integer noAssignedDepartments;
}

//TODO NEED TO REDUCE THE QEURY BY OF ALLOWED DEPARTMENTS BY SENDING THE ALLOWED DEPARTMENT VIA JWT
@Service
public class StaffService {
    private final StaffRepository staffRepository;
    private final BcryptService bcryptService;
    private final DepartmentRepository departmentRepository;

    StaffService(StaffRepository staffRepository, BcryptService bcryptService, ExecutorService executorService, DepartmentRepository departmentRepository){
        this.staffRepository = staffRepository;
        this.bcryptService = bcryptService;
        this.departmentRepository = departmentRepository;
    }

    public boolean isAllowedToViewStaff(Long creatorId, Long viewingId) throws CustomError{
        Staff creatorDetails = isStaffExists(creatorId);
        Staff viewDetails = isStaffExists(viewingId);
        if(creatorDetails.isSuperAdmin()){
            return true;
        }
        HashSet<Department> allowedDepartment = new HashSet<>(creatorDetails.getPermittedDepartments());
        for(Department department: viewDetails.getPermittedDepartments()){
            if(!allowedDepartment.contains(department)){
                return false;
            }
        }
        return true;
    }

    public boolean isAllowedToViewStaff(Long creatorId, List<Long> departmentIdList) throws CustomError{
        Staff creatorDetails = isStaffExists(creatorId);

        if(creatorDetails.isSuperAdmin()){
            return true;
        }

        HashSet<Long> allowedDepartment = new HashSet<>();

        for(Department department: creatorDetails.getPermittedDepartments()){
            allowedDepartment.add(department.getId());
        }

        for(Long departmentId: departmentIdList){
            if(!allowedDepartment.contains(departmentId)){
                return false;
            }
        }

        return true;
    }

    public List<Department> canCreateStaff(Long creatorId, List<Long> creatingDepartments, boolean creatingSuperAdmin) throws CustomError{
        Staff staff = isStaffExists(creatorId);
        if(!staff.isSuperAdmin() && creatingSuperAdmin){
            throw new CustomError(HttpStatus.CONFLICT, "NOT ALLOWED TO PERFORM");
        }
        Map<Long, Department> allowedDepartments = new HashMap<>();

        if(staff.isSuperAdmin()){
            List<Department> superAdminAllowedDeparments = departmentRepository.findByIsDeletedFalse();
            staff.setPermittedDepartments(superAdminAllowedDeparments);
        }

        for(Department department: staff.getPermittedDepartments()){
            allowedDepartments.put(department.getId(), department);
        }

        List<Department> permittedDepartments  = new ArrayList<>();
        for(Long departmentId: creatingDepartments){
            if(allowedDepartments.get(departmentId) ==  null){
                throw new CustomError(HttpStatus.CONFLICT, "Not Allowed To Create");
            }

            permittedDepartments.add(allowedDepartments.get(departmentId));
        }

        return permittedDepartments;
    }

    public void addStaff(String username, String password, String name, RoleEnum role, Boolean isSuperAdmin, List<Department> departments) throws CustomError{
        Optional<Staff> existingStaff = staffRepository.findByUsername(username);
        if(existingStaff.isPresent()){
            throw new CustomError(HttpStatus.CONFLICT, "Username Already Exists");
        }
        String hashPassword = bcryptService.hashPassword(password);
        Staff  newStaff = new Staff(
            username, hashPassword, name, isSuperAdmin, role, departments
        );
        staffRepository.save(newStaff);
    }

    public void changePassword(Long userId, String newPassword) throws CustomError{
        Staff staff = isStaffExists(userId);
        if(staff.isSuperAdmin()){
            throw new CustomError(HttpStatus.CONFLICT, "CANNOT PERFORM ACTION");
        }
        String hashPassword = bcryptService.hashPassword(newPassword);
        staff.setPassword(hashPassword);
        staffRepository.save(staff);
    }

    public void updateDepartment(Long userId, List<Department> permittedDepartments) throws CustomError{
        Staff staff = isStaffExists(userId);
        staff.setPermittedDepartments(permittedDepartments);
        staffRepository.save(staff);
    }

    public GetStaffDTO getStaff(Long staffId) throws CustomError{
        Staff staff = isStaffExists(staffId);
        GetStaffDTO data = new GetStaffDTO();
        data.setId(staff.getId());
        data.setName(staff.getName());
        data.setUsername(staff.getUsername());

        List<GetStaffDTO.DepartmentDTO> departmentDTOList = new ArrayList<>();
        for(Department department: staff.getPermittedDepartments()){
            departmentDTOList.add(new GetStaffDTO.DepartmentDTO(department.getId(), department.getName()));
        }

        data.setAllowedDepartments(departmentDTOList);
        return data;
    }

    public GetAllDTO getAllStaff(Integer page, Integer size, List<Long> departmentIdList){
        Pageable pageRequest = PageRequest.of(page - 1, size, Sort.by("name").ascending());
        Page<Staff> staffList;
        if(departmentIdList == null ||  departmentIdList.isEmpty()){
            staffList = staffRepository.findAll(pageRequest);
        }else{
            staffList = staffRepository.findDistinctByPermittedDepartments_IdIn(departmentIdList, pageRequest);
        }

        List<GetAllStaffDTO> staffDTOList = new ArrayList<>();
        for(Staff staff: staffList.getContent()){
            staffDTOList.add(new GetAllStaffDTO(staff.getId(), staff.getUsername(), staff.getName(), staff.getResolvedIssues().size(), staff.getPermittedDepartments().size()));
        }

        GetAllDTO getAllDTO = new GetAllDTO<>(staffDTOList, page, size, staffList.getTotalPages());
        return getAllDTO;
    }

    public Staff isStaffExists(Long id) throws CustomError {
        return staffRepository.findById(id)
                .orElseThrow(() -> new CustomError(
                        HttpStatus.NOT_FOUND,
                        "Staff Not Found"
                ));
    }



    public Staff isStaffExists(String username) throws CustomError {
        return staffRepository.findByUsername(username)
                .orElseThrow(() -> new CustomError(
                        HttpStatus.NOT_FOUND,
                        "Staff Not Found"
                ));
    }

}
