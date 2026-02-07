package com.example.BlackboxBackend.Utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptService {
    PasswordEncoder passwordEncoder;

    BcryptService(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public String hashPassword(String originalPassword){
        return passwordEncoder.encode(originalPassword);
    }

    public boolean comparePassword(String hashPassword, String enteredPassword){
        return passwordEncoder.matches(enteredPassword, hashPassword);
    }
}
