package com.example.BlackboxBackend.Controller;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Service.DepartmentService;
import com.example.BlackboxBackend.Service.IssueService;
import com.example.BlackboxBackend.Utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

  private final IssueService issueService;


  public IssueController(IssueService issueService) {
    this.issueService = issueService;
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
}
