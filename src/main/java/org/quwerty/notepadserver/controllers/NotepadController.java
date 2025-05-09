package org.quwerty.notepadserver.controllers;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.NameDTO;
import org.quwerty.notepadserver.dto.NotepadInfoDTO;
import org.quwerty.notepadserver.dto.UserAccessDTO;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.exceptions.*;
import org.quwerty.notepadserver.repositories.NotepadRepo;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.quwerty.notepadserver.services.NotepadService;
import org.quwerty.notepadserver.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notepads")
public class NotepadController {

    private final NotepadRepo notepadRepo;
    private final NotepadService notepadService;
    private final UserService userService;
    private final UserRepo userRepo;

    @GetMapping("")
    public ResponseEntity<List<NotepadInfoDTO>> getAllByUser(Principal principal) {
        try {
            var user = userService.findByPrincipal(principal);
            return ResponseEntity.ok(user
                    .getNotepads()
                    .stream()
                    .map(una -> notepadService.toNotepadInfoDTO(
                            una.getNotepad(),
                            una.getAccessType()))
                    .toList());

        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createNotepad(@RequestBody NameDTO name, Principal principal) {
        try {
            var user = userService.findByPrincipal(principal);
            Notepad notepad = notepadService.createNotepad(name.getName(), user);
            return new ResponseEntity<>(
                    notepadService.toNotepadInfoDTO(notepad, AccessType.Admin),
                    HttpStatus.CREATED);

        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (NotepadAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }


    @GetMapping("/{notepad_id}")
    public ResponseEntity<?> getNotepad(
            @PathVariable("notepad_id") int notepadId,
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Notepad notepad = notepadRepo.findNotepadById(notepadId)
                    .orElseThrow(NoSuchNotepadException::new);

            return ResponseEntity.ok(notepadService.toNotepadInfoDTO(
                    notepad,
                    notepadService.getUserAccess(notepad, user)
                            .orElseThrow(ForbiddenException::new)));
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (NoSuchNotepadException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{notepad_id}")
    public ResponseEntity<?> deleteNotepad(
            @PathVariable("notepad_id") int notepadId,
            Principal principal
    ) {
        try {
            Notepad notepad = notepadRepo.findNotepadById(notepadId)
                    .orElseThrow(NoSuchNotepadException::new);
            User user = userService.findByPrincipal(principal);
            notepadService.deleteNotepad(notepad, user);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchNotepadException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{notepad_id}/users")
    public ResponseEntity<?> getUsersFromNotepad(
            @PathVariable int notepad_id,
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Notepad notepad = notepadRepo.findNotepadById(notepad_id)
                    .orElseThrow(NoSuchNotepadException::new);
            if (notepadService.getUserAccess(notepad, user).isPresent()) {

                return new ResponseEntity<>(notepad.getAccessors()
                        .stream()
                        .map(una -> new UserAccessDTO(
                                una.getUser().getId(),
                                una.getAccessType().toString()
                        ))
                        .toList(), HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (NoSuchNotepadException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{notepad_id}/users")
    public ResponseEntity<?> addUserToNotepad(
            @PathVariable int notepad_id,
            Principal principal,
            @RequestBody UserAccessDTO userAccessDTO
    ) {
        try {
            User actionist = userService.findByPrincipal(principal);
            User subject = userRepo.findById(userAccessDTO.getUserId())
                    .orElseThrow(NoSuchUserException::new);
            Notepad notepad = notepadRepo.findNotepadById(notepad_id)
                    .orElseThrow(NoSuchNotepadException::new);
            notepadService.addUserAccess(notepad, actionist, subject,
                    AccessType.valueOf(userAccessDTO.getAccessType()));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (NoSuchNotepadException e) {
            return new ResponseEntity<>("Notepad not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{notepad_id}/users/{user_id}")
    public ResponseEntity<?> updateUserNotepadAccess(
            @PathVariable int notepad_id,
            @PathVariable int user_id,
            @RequestBody UserAccessDTO userAccessDTO,
            Principal principal
    ) {
        try {
            User actionist = userService.findByPrincipal(principal);
            User subject = userRepo.findById(user_id)
                    .orElseThrow(NoSuchUserException::new);
            Notepad notepad = notepadRepo.findNotepadById(notepad_id)
                    .orElseThrow(NoSuchNotepadException::new);
            notepadService.changeUserAccess(
                    notepad,
                    actionist,
                    subject,
                    AccessType.valueOf(userAccessDTO.getAccessType()));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (NoSuchNotepadException e) {
            return new ResponseEntity<>("Notepad not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{notepad_id}/users/{user_id}")
    public ResponseEntity<?> deleteUserFromNotepad(
            @PathVariable int notepad_id,
            @PathVariable int user_id,
            Principal principal
    ) {
        try {
            User actionist = userService.findByPrincipal(principal);
            User subject = userRepo.findById(user_id)
                    .orElseThrow(NoSuchUserException::new);
            Notepad notepad = notepadRepo.findNotepadById(notepad_id)
                    .orElseThrow(NoSuchNotepadException::new);
            notepadService.deleteUserFromNotepad(notepad, actionist, subject);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (NoSuchNotepadException e) {
            return new ResponseEntity<>("Notepad not found", HttpStatus.NOT_FOUND);
        }
    }
}
