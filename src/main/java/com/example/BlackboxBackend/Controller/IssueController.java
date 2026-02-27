package com.example.BlackboxBackend.Controller;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.DTO.IssueStatusEnum;
import com.example.BlackboxBackend.DTO.JwtDTO;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Service.DepartmentService;
import com.example.BlackboxBackend.Service.IssueService;
import com.example.BlackboxBackend.Service.PermissionService;
import com.example.BlackboxBackend.Utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class UpdateIssueDTO{
  @Length(min = 1, message = "Resolved text is needed")
  String resolvedText;
  IssueStatusEnum issueStatusEnum;
}

@RestController
@RequestMapping("/api/issues")
@Validated
public class IssueController {

  private final IssueService issueService;
  private final PermissionService permissionService;


  public IssueController(IssueService issueService, PermissionService permissionService) {
    this.issueService = issueService;
    this.permissionService = permissionService;
  }

  @PostMapping("/submit-issues")
  public ResponseEntity<?> submitIssue(
      @RequestParam("title") String title,
      @RequestParam("content") String content,
      @RequestParam("department") String department,
      @RequestParam(value = "images", required = false) List<MultipartFile> images,
      @RequestParam(value = "audio", required = false) MultipartFile audio) throws CustomError, IOException {

    if (title == null || title.trim().isEmpty()) {
      throw new CustomError(HttpStatus.BAD_REQUEST, "Title is required");
    }
    if (content == null || content.trim().isEmpty()) {
      throw new CustomError(HttpStatus.BAD_REQUEST, "Content is required");
    }
    if (department == null || department.trim().isEmpty()) {
      throw new CustomError(HttpStatus.BAD_REQUEST, "Department is required");
    }


    issueService.submitIssue(title.trim(), content, Long.parseLong(department.trim()), images, audio);

    return ResponseHandler.handleResponse(HttpStatus.OK, null, "Issue submitted successfully");
  }


  @GetMapping("/admin/get-all/{departmentId}")
  public ResponseEntity<?> getAllIssues(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable Long departmentId, @RequestParam int page, @RequestParam int size) throws CustomError {
    permissionService.hasPermission(null, departmentId, jwtDTO.getUserId());
    return ResponseHandler.handleResponse(HttpStatus.OK, issueService.getAllIssues(departmentId, page, size), "All Issue Details");
  }

  @GetMapping("/admin/get-all-history/{departmentId}")
  public ResponseEntity<?> getAllHistoryIssues(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable Long departmentId, @RequestParam int page, @RequestParam int size) throws CustomError {
    permissionService.hasPermission(null, departmentId, jwtDTO.getUserId());
    return ResponseHandler.handleResponse(HttpStatus.OK, issueService.getAllHistoryIssues(departmentId, page, size), "All Issue Details");
  }

  @GetMapping("/admin/get/{issueId}")
  public ResponseEntity<?> getIssues(@AuthenticationPrincipal JwtDTO jwtDTO,@PathVariable String issueId) throws CustomError {
    permissionService.hasPermission(Long.parseLong(issueId), null, jwtDTO.getUserId());
    return ResponseHandler.handleResponse(HttpStatus.OK, issueService.getIssue(Long.parseLong(issueId)), "All Issue Details");
  }

  @PostMapping("/admin/update/{issueId}")
  public ResponseEntity<?> updateIssue(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable Long issueId, @RequestBody @Valid UpdateIssueDTO updateIssueDTO) throws CustomError {
    permissionService.hasPermission(issueId, null, jwtDTO.getUserId());
    issueService.updateIssue(issueId, updateIssueDTO.getResolvedText(), updateIssueDTO.getIssueStatusEnum());
    return ResponseHandler.handleResponse(HttpStatus.OK, null, "Issue Status Update");
  }
}
