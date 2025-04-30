package org.quwerty.notepadserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.quwerty.notepadserver.domain.entity.user.User;
import org.quwerty.notepadserver.repository.UserRepo;
import org.quwerty.notepadserver.requestObject.RegisterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    HttpServletResponse httpServletResponse;

    @GetMapping("/")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userRepo.findById(id).orElse(null);
    }

    @PostMapping("/")
    public User createUser(@RequestBody RegisterData registerData) {
        httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        return userRepo.save(new User(registerData.getUsername(), registerData.getEmail()));
    }
}
