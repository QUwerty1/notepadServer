package org.quwerty.notepadserver.controllers;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.UserDTO;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepo userRepo;

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
