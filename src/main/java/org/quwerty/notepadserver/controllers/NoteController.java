package org.quwerty.notepadserver.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.UserAccessDTO;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.note.Note;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNoteAccess;
import org.quwerty.notepadserver.exceptions.ForbiddenException;
import org.quwerty.notepadserver.exceptions.NoSuchNoteException;
import org.quwerty.notepadserver.exceptions.NoSuchUserException;
import org.quwerty.notepadserver.exceptions.NoteAlreadyExistsException;
import org.quwerty.notepadserver.repositories.NoteRepo;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.quwerty.notepadserver.services.NoteService;
import org.quwerty.notepadserver.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.quwerty.notepadserver.dto.NoteDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final UserService userService;
    private final NoteService noteService;
    private final NoteRepo noteRepo;
    private final UserRepo userRepo;

    @GetMapping("/{note_id}")
    public ResponseEntity<?> getNote(
            @PathVariable int note_id,
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Note note = noteService.findById(note_id, user);

            return ResponseEntity.ok(noteService.toNoteDTO(note));

        } catch (NoSuchNoteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{note_id}")
    public ResponseEntity<?> deleteNote(
            @PathVariable("note_id") int noteId,
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Note note = noteService.findById(noteId, user);
            noteService.deleteNote(user, note);

            return ResponseEntity.ok().build();

        } catch (NoSuchNoteException e) {
            return ResponseEntity.notFound().build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{note_id}")
    public ResponseEntity<?> updateNote(
            @PathVariable("note_id") int noteId,
            Principal principal,
            @RequestBody @Valid NoteDTO noteDTO
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            noteDTO.setId(noteId);
            noteService.updateNote(user, noteDTO);

            return ResponseEntity.ok().build();

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchNoteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NoteAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{note_id}/users")
    public ResponseEntity<?> getAccessorsByNotes(
            @PathVariable int note_id,
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Note note = noteRepo.findById(note_id).orElseThrow(NoSuchNoteException::new);
            List<UserNoteAccess> users = noteService.getAccessorsByNote(user, note);
            return ResponseEntity.ok(
                    users.stream()
                            .map(noteService::noteAccessToDTO)
                            .toList()
            );
        } catch (NoSuchNoteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/{note_id}/users")
    public ResponseEntity<?> createAccessor(
            @PathVariable int note_id,
            Principal principal,
            @RequestBody UserAccessDTO accessDTO
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Note note = noteRepo.findById(note_id).orElseThrow(NoSuchNoteException::new);

            UserNoteAccess noteAccess = noteService.toEntity(accessDTO, note);
            noteService.addAccessor(user, noteAccess);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (NoSuchNoteException | NoSuchUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{note_id}/users/{user_id}")
    public ResponseEntity<?> updateAccessor(
            @PathVariable int note_id,
            @PathVariable int user_id,
            Principal principal,
            @RequestBody String noteAccess
    ) {
        try {
            User actor = userService.findByPrincipal(principal);
            User subject = userRepo.findById(user_id).orElseThrow(NoSuchUserException::new);
            Note note = noteRepo.findById(note_id).orElseThrow(NoSuchNoteException::new);

            noteService.changeAccessor(actor, subject, note, AccessType.valueOf(noteAccess));

            return ResponseEntity.ok().build();

        } catch (NoSuchNoteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{note_id}/lock")
    public ResponseEntity<?> lock(
            @PathVariable int note_id,
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Note note = noteRepo.findById(note_id).orElseThrow(NoSuchNoteException::new);

            noteService.lock(user, note);

            return ResponseEntity.ok().build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NoSuchNoteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{note_id}/unlock")
    public ResponseEntity<?> unlock(
            @PathVariable int note_id,
            Principal principal
    ) {
        try {
            User user = userService.findByPrincipal(principal);
            Note note = noteRepo.findById(note_id).orElseThrow(NoSuchNoteException::new);

            noteService.unlock(user, note);

            return ResponseEntity.ok().build();

        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NoSuchNoteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
