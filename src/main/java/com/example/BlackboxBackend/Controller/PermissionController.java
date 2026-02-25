package com.example.BlackboxBackend.Controller;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.DTO.JwtDTO;
import com.example.BlackboxBackend.Service.PermissionService;
import com.example.BlackboxBackend.Utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permission/admin")
public class PermissionController {
    PermissionService permissionService;

    PermissionController(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @GetMapping("/check-page/{permission}")
    ResponseEntity<?> checkPermissionPage(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable String permission) throws CustomError {
        if(permissionService.checkPermissionPage(jwtDTO.getUserId(), permission)){
            return ResponseHandler.handleResponse(HttpStatus.OK, null, "PERMISSION ALLOWED");
        }
        return ResponseHandler.handleResponse(HttpStatus.UNAUTHORIZED, null, "PERMISSION DENIED");
    }

    @GetMapping("/check-department/{departmentId}")
    ResponseEntity<?> checkPermissionDepartment(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable Long departmentId) throws CustomError {
        if(permissionService.checkPermissionDepartment(jwtDTO.getUserId(), departmentId)){
            return ResponseHandler.handleResponse(HttpStatus.OK, null, "PERMISSION ALLOWED");
        }
        return ResponseHandler.handleResponse(HttpStatus.UNAUTHORIZED, null, "PERMISSION DENIED");
    }

    @GetMapping("/get-permitted-department")
    ResponseEntity<?> getPermittedDepartment(@AuthenticationPrincipal JwtDTO jwtDTO) throws CustomError {
        return ResponseHandler.handleResponse(HttpStatus.OK, permissionService.getPermittedDepartmented(jwtDTO.getUserId()), "Permitted Departments");
    }
}
