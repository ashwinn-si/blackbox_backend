# Image Storage and Retrieval System Documentation

## Overview

This document explains the image storage and retrieval mechanisms implemented in the BlackboxBackend application, specifically in the `IssueService` and `IssueController` classes. The system handles file uploads, storage, retrieval, and includes error handling and transaction management.

## Image Storage Architecture

### Storage Strategy
The application uses a **file system-based storage approach** with the following characteristics:

- **Physical Storage**: Images are stored as files on the local file system
- **Directory Structure**: 
  - Images: `uploads/image/`
  - Audio files: `uploads/audio/`
- **Database Storage**: Only file paths are stored in the database, not the binary data
- **File Naming**: UUID-based unique filenames with original file extensions

### Storage Process

#### 1. File Upload Flow
```java
@PostMapping("/submit-issues")
public ResponseEntity<?> submitIssue(
    @RequestParam("title") String title,
    @RequestParam("content") String content,
    @RequestParam("department") String department,
    @RequestParam(value = "images", required = false) List<MultipartFile> images,
    @RequestParam(value = "audio", required = false) MultipartFile audio)
```

#### 2. File Processing in Service Layer
The `submitIssue` method in `IssueService` handles the complete storage workflow:

1. **Validation**: Department existence validation
2. **Entity Creation**: Issue entity setup
3. **File Storage**: Physical file saving with unique names
4. **Database Persistence**: Storing file paths and relationships
5. **Transaction Management**: Ensuring data consistency

#### 3. Unique File Naming
```java
private String saveFile(MultipartFile file, String uploadDir) throws IOException {
    String originalFilename = file.getOriginalFilename();
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    String uniqueFilename = UUID.randomUUID().toString() + extension;
    // ...
}
```

**Benefits of UUID naming:**
- Prevents filename conflicts
- Maintains original file extensions
- Ensures uniqueness across the system
- Prevents path traversal attacks

## Image Retrieval System

### Retrieval Process

#### 1. API Endpoint
```java
@GetMapping("/admin/get/{issueId}")
public ResponseEntity<?> getIssues(@AuthenticationPrincipal JwtDTO jwtDTO, @PathVariable String issueId)
```

#### 2. Service Layer Retrieval
The `getIssue` method handles complete data retrieval:

1. **Issue Lookup**: Database query by issue ID
2. **File Reading**: Converting stored files to byte arrays
3. **Data Transformation**: Creating DTOs with binary data
4. **Response Formatting**: Preparing data for API consumption

#### 3. File-to-Binary Conversion
```java
private byte[] readFileToBytes(String filePath) {
    try {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    } catch (IOException e) {
        System.err.println("Failed to read file as bytes: " + filePath);
        return null;
    }
}
```

### Data Transfer Objects (DTOs)

#### IssueDTO Structure
```java
class IssueDTO {
    private Long id;
    private String title;
    private String content;
    private String resolution;
    private List<byte[]> images;  // Binary data for frontend
    private byte[] audio;         // Binary data for frontend
    private Date createdAt;
    private Date resolvedAt;
}
```

## Error Handling Mechanisms

### 1. Transaction-Based Error Handling

#### Transactional Integrity
```java
@Transactional
public void submitIssue(String title, String content, Long departmentId,
    List<MultipartFile> images, MultipartFile audio) throws CustomError, IOException
```

**Key Features:**
- Automatic database rollback on exceptions
- Manual file cleanup on transaction failure
- Maintains data consistency between database and file system

#### File Cleanup on Failure
```java
try {
    // File upload and database operations
    issueRepository.save(issue);
} catch (Exception e) {
    // Roll back any files that were uploaded to disk
    for (String filePath : uploadedFilePaths) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ioEx) {
            System.err.println("Failed to delete uploaded file during rollback: " + filePath);
        }
    }
    throw e;
}
```

### 2. Input Validation

#### Controller-Level Validation
```java
if (title == null || title.trim().isEmpty()) {
    throw new CustomError(HttpStatus.BAD_REQUEST, "Title is required");
}
if (content == null || content.trim().isEmpty()) {
    throw new CustomError(HttpStatus.BAD_REQUEST, "Content is required");
}
if (department == null || department.trim().isEmpty()) {
    throw new CustomError(HttpStatus.BAD_REQUEST, "Department is required");
}
```

#### Business Logic Validation
```java
Department department = departmentRepository
    .findByIdAndIsDeletedFalse(departmentId)
    .orElseThrow(() -> new CustomError(HttpStatus.NOT_FOUND, "Department not found: " + departmentId));
```

### 3. File Operation Error Handling

#### Safe File Reading
- Graceful handling of missing files
- Returns `null` for unreadable files
- Logs errors without breaking the application flow

#### Directory Creation
```java
Path dirPath = Paths.get(uploadDir);
if (!Files.exists(dirPath)) {
    Files.createDirectories(dirPath);
}
```

## Current Limitations and Areas for Improvement

### Redundancy Considerations

#### Current State
- **Single Point of Failure**: Files stored only on local file system
- **No Backup Strategy**: No automated backup mechanisms
- **No Replication**: Single instance storage

#### Recommended Improvements

1. **Cloud Storage Integration**
   - Migrate to AWS S3, Azure Blob Storage, or Google Cloud Storage
   - Implement automatic backup and versioning
   - Enable cross-region replication

2. **Database Backup Strategy**
   - Regular database backups including file path metadata
   - Point-in-time recovery capabilities

3. **File Integrity Checks**
   - Implement checksums for uploaded files
   - Periodic integrity verification
   - Automated repair mechanisms

### Security Considerations

#### Current Security Measures
- UUID-based file naming prevents guessing
- Path traversal protection through controlled directory structure
- Authentication required for admin endpoints

#### Additional Security Recommendations
- File type validation and sanitization
- Virus scanning for uploaded files
- Content-based security policies
- Rate limiting for file uploads

### Performance Optimizations

#### Current Performance Characteristics
- Direct file system I/O
- Full file loading into memory for responses
- No caching mechanisms

#### Optimization Opportunities
- Implement file caching
- Stream large files instead of loading entirely
- Thumbnail generation for images
- CDN integration for faster delivery

## Monitoring and Maintenance

### Logging Strategy
- File operation logging for audit trails
- Error logging with detailed context
- Performance monitoring for large file operations

### Maintenance Tasks
- Regular cleanup of orphaned files
- Disk space monitoring
- File system health checks
- Database-filesystem consistency verification

## Conclusion

The current implementation provides a solid foundation for image storage and retrieval with basic error handling and transaction management. However, for production environments, consider implementing the recommended improvements for redundancy, security, and performance optimization.