package com.example.BlackboxBackend.Service;


import com.example.BlackboxBackend.DTO.CustomError;
import com.example.BlackboxBackend.Domain.Staff;
import com.example.BlackboxBackend.Repository.StaffRepository;
import com.example.BlackboxBackend.Utils.BcryptService;
import com.example.BlackboxBackend.Utils.JwtService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Data
@AllArgsConstructor
@NoArgsConstructor
class StaffDTO{
    private String username;
    private String name;
    private String token;
}

@Service
public class AuthService {
    private final StaffService staffService;
    private final BcryptService bcryptService;
    private final JwtService jwtService;

    AuthService(StaffService staffService, BcryptService bcryptService, JwtService jwtService){
        this.staffService = staffService;
        this.jwtService = jwtService;
        this.bcryptService = bcryptService;
    }


    public StaffDTO login(String username, String password) throws CustomError {
        Staff staff = staffService.isStaffExists(username);
        boolean passwordResult = bcryptService.comparePassword(staff.getPassword(), password);

        if(!passwordResult){
            throw new CustomError(HttpStatus.CONFLICT, "Password Incorrect");
        }

        String token = jwtService.generateToken(staff.getId(), staff.isSuperAdmin());

        return new StaffDTO(staff.getUsername(), staff.getName(), token);
    }

}
