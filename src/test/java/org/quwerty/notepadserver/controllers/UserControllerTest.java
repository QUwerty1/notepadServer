package org.quwerty.notepadserver.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quwerty.notepadserver.dto.UserDTO;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final User user = new User(1, "User1", "u1@mail.com");
    private final Principal principal = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserController userController;

    @Test
    void getUsersT5() {
        var users = List.of(
                new User(1, "User1", "u1@mail.com"),
                new User(2, "User2", "u2@mail.com")
        );
        when(userRepo.findAll()).thenReturn(users);

        var result = userController.getUsers();

        assertEquals(
                users.stream().map(user -> UserDTO
                                .builder()
                                .id(user.getId())
                                .name(user.getUsername())
                                .email(user.getEmail())
                                .build())
                        .toList(),
                result.getBody());

        assertEquals(result.getStatusCode().value(), HttpStatus.OK.value());
    }

    @Test
    void getCurrentUserT6() {
//        when(userRepo.findByUsername(principal.getName())).thenReturn(Optional.of(user));
//
//        var result = userController.getCurrentUser(principal);
//        assertEquals(result.getStatusCode().value(), HttpStatus.OK.value());
//        assertEquals(result.getBody(), user);
    }

    @Test
    void getUserByIdSuccessT7() {
    }

    @Test
    void getUserById404T8() {
    }
}