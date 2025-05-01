package org.quwerty.notepadserver.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.quwerty.notepadserver.dto.RegStructDTO;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.exceptions.UserAlreadyExistsException;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("Пользователь %s не найден", username)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }

    @Transactional
    public void createUser(RegStructDTO rs) {

        if (userRepo.findByUsername(rs.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("");
        }
        User user = User.builder()
                .email(rs.getEmail())
                .username(rs.getUsername())
                .password(passwordEncoder.encode(rs.getPassword()))
                .roles(List.of())
                .build();

        userRepo.save(user);
    }
}
