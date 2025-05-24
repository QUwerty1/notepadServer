package org.quwerty.notepadserver.controllers;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.entities.note.Note;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.exceptions.ForbiddenException;
import org.quwerty.notepadserver.exceptions.NoSuchNoteException;
import org.quwerty.notepadserver.services.NoteService;
import org.quwerty.notepadserver.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final UserService userService;
    private final NoteService noteService;

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
}
