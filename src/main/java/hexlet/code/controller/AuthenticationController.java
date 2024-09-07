package hexlet.code.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hexlet.code.dto.AuthRequest;
import hexlet.code.util.JWTUtils;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AuthenticationController {
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String create(@RequestBody @Valid AuthRequest authRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword());
        authenticationManager.authenticate(authentication);

        var token = jwtUtils.generateToken(authRequest.getUsername());
        return token;
    }
}
