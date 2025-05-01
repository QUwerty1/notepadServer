package org.quwerty.notepadserver.controllers;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.AuthStructDTO;
import org.quwerty.notepadserver.dto.JwtTokenDTO;
import org.quwerty.notepadserver.dto.RegStructDTO;
import org.quwerty.notepadserver.services.UserService;
import org.quwerty.notepadserver.utils.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthStructDTO as) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(as.getUsername(), as.getPassword())
            );
        } catch (BadCredentialsException bce) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(as.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);

        return new ResponseEntity<>(new JwtTokenDTO(token), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegStructDTO rs) {
        if (userService.findByUsername(rs.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                    "Пользователь с таким именем уже существует",
                    HttpStatus.UNAUTHORIZED);
        }
        userService.createUser(rs);

        UserDetails userDetails = userService.loadUserByUsername(rs.getUsername());

        return new ResponseEntity<>(new JwtTokenDTO(jwtTokenUtil.generateToken(userDetails)),
                HttpStatus.CREATED);
    }
}
