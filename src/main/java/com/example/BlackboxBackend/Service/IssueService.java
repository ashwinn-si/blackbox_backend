package com.example.BlackboxBackend.Service;

import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.DTO.IssueStatusEnum;
import com.example.BlackboxBackend.Domain.Department;
import com.example.BlackboxBackend.Domain.Image;
import com.example.BlackboxBackend.Domain.Issue;
import com.example.BlackboxBackend.Repository.DepartmentRepository;
import com.example.BlackboxBackend.Repository.IssueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

  private final IssueRepository issueRepository;
  private final DepartmentRepository departmentRepository;

  private static final String IMAGE_UPLOAD_DIR = "uploads/image";
  private static final String AUDIO_UPLOAD_DIR = "uploads/audio";

  public IssueService(IssueRepository issueRepository, DepartmentRepository departmentRepository) {
    this.issueRepository = issueRepository;
    this.departmentRepository = departmentRepository;
  }

  @Transactional
  public void submitIssue(String title, String content, Long departmentId,
      List<MultipartFile> images, MultipartFile audio) throws CustomError, IOException {

    // Track all uploaded file paths so we can roll them back on failure
    List<String> uploadedFilePaths = new ArrayList<>();

    try {
      // Find department by id
      Department department = departmentRepository
          .findByIdAndIsDeletedFalse(departmentId)
          .orElseThrow(() -> new CustomError(HttpStatus.NOT_FOUND, "Department not found: " + departmentId));

      // Build the issue entity
      Issue issue = new Issue();
      issue.setTitle(title);
      issue.setContent(content);
      issue.setDepartment(department);
      issue.setStatus(IssueStatusEnum.CREATED);
      issue.setCreatedAt(Date.valueOf(LocalDate.now()));

      // Save audio file if present
      if (audio != null && !audio.isEmpty()) {
        String audioPath = saveFile(audio, AUDIO_UPLOAD_DIR);
        uploadedFilePaths.add(audioPath);
        issue.setAudio(audioPath);
      }

      // Save images if present - prepare them before saving the issue
      if (images != null && !images.isEmpty()) {
        for (MultipartFile imageFile : images) {
          if (!imageFile.isEmpty()) {
            String imagePath = saveFile(imageFile, IMAGE_UPLOAD_DIR);
            uploadedFilePaths.add(imagePath);
            Image image = new Image();
            image.setImagePath(imagePath);
            image.setIssue(issue);
            issue.getImages().add(image);
          }
        }
      }

      // Save the issue once with all relationships established
      // If this throws, the catch block will clean up uploaded files
      issueRepository.save(issue);

    } catch (Exception e) {
      // Roll back any files that were uploaded to disk
      for (String filePath : uploadedFilePaths) {
        try {
          Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ioEx) {
          // Log but don't suppress the original exception
          System.err.println("Failed to delete uploaded file during rollback: " + filePath + " — " + ioEx.getMessage());
        }
      }
      throw e;
    }
  }

  /**
   * Saves a multipart file to the given directory with a unique filename.
   * Returns the relative path to the saved file.
   */
  private String saveFile(MultipartFile file, String uploadDir) throws IOException {
    // Ensure the upload directory exists
    Path dirPath = Paths.get(uploadDir);
    if (!Files.exists(dirPath)) {
      Files.createDirectories(dirPath);
    }

    // Generate a unique filename
    String originalFilename = file.getOriginalFilename();
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    String uniqueFilename = UUID.randomUUID().toString() + extension;

    // Save the file
    Path filePath = dirPath.resolve(uniqueFilename);
    Files.copy(file.getInputStream(), filePath);

    return uploadDir + "/" + uniqueFilename;
  }
}
