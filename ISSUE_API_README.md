# IssueController API Endpoints

This document describes the endpoints provided by the `IssueController` for managing issues in the system.


## 2. Update Issue (Admin)

- **Endpoint:** `POST /api/issues/admin/update/{issueId}`
- **Description:** Update the status and resolution text of an issue (admin access).
- **Path Variable:**
  - `issueId` (Long): Issue ID
- **Authentication:** Requires a valid JWT
- **Request Body (JSON):**
  ```json
  {
    "resolvedText": "string (required)",
    "issueStatusEnum": "CREATED | RESOLVED | SPAM"
  }
  ```
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "status": 200,
      "data": null,
      "message": "Issue Status Update"
    }
    ```
  - **Error:** 400 BAD REQUEST if required fields are missing or invalid, or permission denied.

---


## 2. Get All Issues (Admin)

- **Endpoint:** `GET /api/issues/admin/get-all/{departmentId}`
- **Description:** Retrieve all issues for a department (admin access).
- **Path Variable:**
  - `departmentId` (Long): Department ID
- **Query Parameters:**
  - `page` (int): Page number
  - `size` (int): Page size
- **Authentication:** Requires a valid JWT (user info extracted from token)
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "status": 200,
      "data": {
        "content": [
          {
            "title": "string",
            "createdAt": "yyyy-mm-dd",
            "resolvedAt": null
          }
        ],
        "page": 1,
        "size": 10,
        "totalPages": 5
      },
      "message": "All Issue Details"
    }
    ```
  - **Error:** Permission denied or invalid parameters

---


## 3. Get All History Issues (Admin)

- **Endpoint:** `GET /api/issues/admin/get-all-history/{departmentId}`
- **Description:** Retrieve all historical issues for a department (admin access).
- **Path Variable:**
  - `departmentId` (Long): Department ID
- **Query Parameters:**
  - `page` (int): Page number
  - `size` (int): Page size
- **Authentication:** Requires a valid JWT
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "status": 200,
      "data": {
        "content": [
          {
            "title": "string",
            "createdAt": "yyyy-mm-dd",
            "resolvedAt": "yyyy-mm-dd"
          }
        ],
        "page": 1,
        "size": 10,
        "totalPages": 5
      },
      "message": "All Issue Details"
    }
    ```
  - **Error:** Permission denied or invalid parameters

---


## 4. Get Issue by ID (Admin)

- **Endpoint:** `GET /api/issues/admin/get/{issueId}`
- **Description:** Retrieve details of a specific issue by its ID (admin access).
- **Path Variable:**
  - `issueId` (Long): Issue ID
- **Authentication:** Requires a valid JWT
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "status": 200,
      "data": {
        "title": "string",
        "content": "string",
        "images": ["/uploads/image/uuid.jpg"],
        "audio": "/uploads/audio/uuid.mp3",
        "createdAt": "yyyy-mm-dd",
        "resolvedAt": "yyyy-mm-dd or null"
      },
      "message": "All Issue Details"
    }
    ```
  - **Error:** Permission denied or invalid parameters

---


## Error Handling
- All endpoints may return a custom error object with HTTP status and message if validation fails or permissions are insufficient. Example:
  ```json
  {
    "status": 400,
    "data": null,
    "message": "Error message here"
  }
  ```

---

## Notes
- All admin endpoints require proper permissions, validated via JWT and the `PermissionService`.
- Use appropriate HTTP headers for authentication.
