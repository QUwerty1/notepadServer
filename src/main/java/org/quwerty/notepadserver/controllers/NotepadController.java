package org.quwerty.notepadserver.controllers;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.NotepadInfoDTO;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.note.Note;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNoteAccess;
import org.quwerty.notepadserver.entities.user.UserNotepadAccess;
import org.quwerty.notepadserver.repositories.NotepadRepo;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notepads")
public class NotepadController {

    private final NotepadRepo notepadRepo;
    private final UserRepo userRepo;

    @GetMapping("")
    public ResponseEntity<List<NotepadInfoDTO>> getAllByUser(Principal principal) {
        return ResponseEntity.ok(userRepo
                .findByUsername(principal.getName())
                .get()
                .getNotepads()
                .stream()
                .map(n -> NotepadInfoDTO.builder()
                        .notepadId(n.getNotepad().getId())
                        .notepadName(n.getNotepad().getName())
                        .createdAt(n.getNotepad().getCreatedAt())
                        .updatedAt(n.getNotepad().getUpdatedAt())
                        .accessType(n.getAccessType().toString())
                        .build()
                ).toList());
    }

    @PostMapping("")
    public ResponseEntity<?> createNotepad(@RequestBody String name, Principal principal) {
        if (notepadRepo.findNotepadByName(name).isPresent()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        } else {
            User user = userRepo.findByUsername(principal.getName()).get();
            Notepad notepad = new Notepad(
                    name, user);

            notepadRepo.save(notepad);
            return new ResponseEntity<>(new NotepadInfoDTO(
                    notepad.getId(),
                    notepad.getName(),
                    notepad.getCreatedAt(),
                    notepad.getUpdatedAt(),
                    notepad.getAccessors()
                            .stream()
                            .filter(au -> au.getUser() == user)
                            .toList()
                            .getFirst()
                            .getAccessType()
                            .toString(),
                    List.of()
            ), HttpStatus.CREATED);
        }
    }


    @GetMapping("/{notepad_id}")
    public ResponseEntity<?> getNotepad(
            @PathVariable("notepad_id") int notepadId,
            Principal principal
    ) {
        User user = userRepo.findByUsername(principal.getName()).get();
        var optNotepad = notepadRepo.findNotepadById(notepadId);
        if (optNotepad.isPresent()) {
            Notepad notepad = optNotepad.get();
            return ResponseEntity.ok(new NotepadInfoDTO(
                    notepad.getId(),
                    notepad.getName(),
                    notepad.getCreatedAt(),
                    notepad.getUpdatedAt(),
                    notepad.getAccessors()
                            .stream()
                            .filter(au -> au.getUser() == user)
                            .toList()
                            .getFirst()
                            .getAccessType()
                            .toString(),
                    notepad.getNotes()
                            .stream()
                            .map(Note::getId)
                            .toList()
            ));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{notepad_id}")
    public ResponseEntity<?> deleteNotepad(@PathVariable("notepad_id") int notepadId) {
        return ResponseEntity.noContent().build();
    }
}
