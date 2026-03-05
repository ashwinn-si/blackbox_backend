# Blackbox Backend - Security Implementation Guide

## Overview

The Blackbox Backend implements a comprehensive multi-layered security system using JWT (JSON Web Tokens) for authentication and a role-based access control (RBAC) system for authorization. The security architecture is built on Spring Security framework and provides fine-grained access control across different resources and operations.

## Security Architecture

### 1. Authentication Layer

#### JWT-Based Authentication
- **Token Generation**: Custom JWT tokens containing user ID, role, and super admin status
- **Token Validation**: Automatic validation on protected routes
- **Token Structure**: 
  ```json
  {
    "userId": 123,
    "role": "ADMIN", 
    "isSuperAdmin": false,
    "iat": 1234567890,
    "exp": 1234567890
  }
  ```
- **Expiration**: 15 hours (54,000,000 ms)
- **Secret Key**: Configured in `JwtService` (should be moved to environment variables)

#### Login Process
1. User submits username and password to `/api/auth/login`
2. System validates credentials against database using BCrypt
3. Upon successful validation, JWT token is generated
4. Token is returned along with user permissions and department access

### 2. Authorization Layer

#### Role-Based Access Control (RBAC)

The system implements a three-tier role hierarchy:

##### **SUPER_ADMIN** 
- **Permissions**: Full system access
- **Capabilities**:
  - Create and manage departments
  - Create and manage all staff members
  - Resolve issues across all departments
  - Access main analytics dashboard
  - Access department-wise analytics
  - View issue history
- **Department Access**: All departments (automatically granted)

##### **ADMIN**
- **Permissions**: Departmental management
- **Capabilities**:
  - Create staff within assigned departments only
  - Resolve issues within assigned departments
  - Access department analytics dashboard
  - View issue history for assigned departments
- **Department Access**: Limited to specific assigned departments

##### **WORKER** 
- **Permissions**: Issue handling only
- **Capabilities**:
  - Respond to and resolve issues within assigned departments
  - View issues assigned to them
- **Department Access**: Limited to specific assigned departments

#### Permission Matrix

| Feature/Screen | SUPER_ADMIN | ADMIN | WORKER |
|----------------|-------------|-------|--------|
| Main Analytics Dashboard | ✅ | ❌ | ❌ |
| Department Analytics Dashboard | ✅ | ✅ | ❌ |
| Issues Management | ✅ | ✅ | ✅ |
| Issue History | ✅ | ✅ | ❌ |
| Staff Management | ✅ | ✅* | ❌ |
| Department Management | ✅ | ❌ | ❌ |

*Admin can only manage staff within their assigned departments

### 3. Security Filters

#### JWT Handler (`JwtHandler`)
- **Purpose**: Validates JWT tokens and sets up security context
- **Trigger**: Activated for URLs containing "admin" or "protected"
- **Process**:
  1. Extracts Bearer token from Authorization header
  2. Validates token signature and expiration
  3. Parses claims (userId, role, isSuperAdmin)
  4. Sets up Spring Security Authentication context
- **Error Handling**: Returns 401 Unauthorized for invalid/missing tokens

#### Permission Handler (`PermissionHandler`) 
- **Purpose**: Enforces role-based access to specific resources
- **Trigger**: Activated for URLs containing "admin" or "protected"
- **Rules**:
  - `/department/**`: Only SUPER_ADMIN access
  - `/staff/**`: SUPER_ADMIN and ADMIN access
  - Super Admin bypass: isSuperAdmin flag allows access to all resources
- **Error Handling**: Returns 401 Unauthorized for insufficient permissions

### 4. Security Configuration

#### Spring Security Setup (`SecurityConfig`)

```java
// Key Configuration Elements:
- CSRF disabled (for stateless JWT APIs)
- CORS enabled (configured for localhost:5173)
- BCrypt password encoding (salt strength: 12)
- Filter chain: JWT → Permission validation
```

#### Protected Routes
- **Admin Routes**: `/api/*/admin/**` (requires authentication + role-based authorization)
- **Protected Routes**: Any URL containing "protected" 
- **Public Routes**: All other endpoints
- **Preflight Requests**: OPTIONS method permitted for all routes

### 5. Department-Based Access Control

#### Department Mapping
- Users are assigned to specific departments via `staff_department_mapping` table
- Super Admins automatically have access to all departments
- Regular users (ADMIN/WORKER) are restricted to their assigned departments

#### Access Logic
```java
// Super Admin: Access all departments
List<Department> departments = departmentRepository.findByIsDeletedFalse();

// Regular User: Access assigned departments only  
List<Department> departments = staff.getPermittedDepartments();
```

### 6. Password Security

#### BCrypt Implementation
- **Algorithm**: BCrypt with salt rounds = 12
- **Service**: `BcryptService` handles encoding and validation
- **Storage**: Only hashed passwords are stored in database
- **Validation**: Plain text passwords are compared against stored hash during login

### 7. Error Handling

#### Global Exception Handler (`GlobalExceptionHandler`)
- **Custom Errors**: `CustomError` class for application-specific errors
- **Validation Errors**: Method argument validation exceptions
- **Security Errors**: 401 Unauthorized responses for auth failures
- **General Errors**: 500 Internal Server Error for unexpected exceptions

### 8. Security Headers and CORS

#### CORS Configuration
```java
// Allowed Origins
config.setAllowedOrigins(List.of("http://localhost:5173"));

// Credentials Support  
config.setAllowCredentials(true);

// Headers and Methods
config.setAllowedHeaders(List.of("*"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
```

## Security Implementation Details

### Authentication Flow Diagram
```
User Login Request
       ↓
Username/Password Validation  
       ↓
BCrypt Password Verification
       ↓
JWT Token Generation
       ↓ 
Return Token + User Permissions
```

### Authorization Flow Diagram  
```
Protected API Request
       ↓
JWT Handler (Token Validation)
       ↓
Permission Handler (Role Check)
       ↓
Department Access Validation
       ↓
Allow/Deny Resource Access
```

## Security Best Practices Implemented

✅ **Token-based stateless authentication**  
✅ **Strong password hashing (BCrypt)**  
✅ **Role-based access control**  
✅ **Department-level data isolation**  
✅ **Input validation and error handling**  
✅ **CORS configuration**  
✅ **Structured permission model**

## Security Improvements Recommended

🔧 **Environment Variables**: Move JWT secret to environment configuration  
🔧 **Token Refresh**: Implement refresh token mechanism  
🔧 **Rate Limiting**: Add API rate limiting for login attempts  
🔧 **Audit Logging**: Implement security event logging  
🔧 **HTTPS Enforcement**: Ensure HTTPS in production  
🔧 **Session Management**: Add token invalidation/logout functionality

## Configuration Files

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | Main Spring Security configuration |
| `JwtHandler.java` | JWT token validation filter |
| `PermissionHandler.java` | Role-based authorization filter |
| `JwtService.java` | JWT token generation and validation utilities |
| `AuthService.java` | Authentication business logic |
| `PermissionService.java` | Permission checking utilities |
| `Constant.java` | Role-to-screen permission mappings |

## Testing Security

### Authentication Testing
```bash
# Login Request
POST /api/auth/login
Content-Type: application/json
{
    "username": "admin",
    "password": "password" 
}

# Expected Response
{
    "data": {
        "username": "admin",
        "name": "Admin User", 
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "allowedDepartments": [...],
        "screens": [...],
        "role": "ADMIN"
    }
}
```

### Authorization Testing
```bash
# Protected Route Access
GET /api/v1/admin/departments
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

# Expected: 200 OK for SUPER_ADMIN, 401 for others
```

This security implementation provides robust protection for the Blackbox Backend application with clear separation of concerns and proper access controls.