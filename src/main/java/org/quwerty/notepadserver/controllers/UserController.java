package org.quwerty.notepadserver.controllers;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.UserDTO;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.quwerty.notepadserver.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepo userRepo;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userRepo.findAll()
                .stream().map(user -> UserDTO
                        .builder()
                        .id(user.getId())
                        .name(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .toList());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            return ResponseEntity.ok(UserDTO
                    .builder()
                    .id(user.getId())
                    .name(user.getUsername())
                    .email(user.getEmail())
                    .build());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id) {
        var userOption = userRepo.findById(id);
        if (userOption.isPresent()) {
            User user = userOption.get();
            return new ResponseEntity<>(
                    UserDTO
                    .builder()
                    .id(user.getId())
                    .name(user.getUsername())
                    .email(user.getEmail())
                            .build(),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
