package org.quwerty.notepadserver.controllers;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.NameDTO;
import org.quwerty.notepadserver.dto.NotepadInfoDTO;
import org.quwerty.notepadserver.dto.UserAccessDTO;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.exceptions.ForbiddenException;
import org.quwerty.notepadserver.exceptions.NoSuchNotepadException;
import org.quwerty.notepadserver.exceptions.NoSuchUserException;
import org.quwerty.notepadserver.exceptions.NotepadAlreadyExistsException;
import org.quwerty.notepadserver.repositories.NotepadRepo;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.quwerty.notepadserver.services.NotepadService;
import org.quwerty.notepadserver.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

        } catch (UsernameNotFoundException e) {
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

        } catch (UsernameNotFoundException e) {
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
        } catch (UsernameNotFoundException e) {
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
        } catch (UsernameNotFoundException e) {
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
        } catch (UsernameNotFoundException e) {
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
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchUserException | NoSuchNotepadException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
