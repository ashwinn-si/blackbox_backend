package com.example.BlackboxBackend.Controller;


import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.Service.AuthService;
import com.example.BlackboxBackend.Utils.ResponseHandler;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@AllArgsConstructor
@NoArgsConstructor
class AuthDTO{
    @NotNull(message = "Username is Required")
    private String username;

    @NotNull(message = "Password is Required")
    private String password;
}

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDTO authDTO) throws CustomError {
        return ResponseHandler.handleResponse(HttpStatus.OK, authService.login(authDTO.getUsername(), authDTO.getPassword()), "Login Successfull");
    }

}
