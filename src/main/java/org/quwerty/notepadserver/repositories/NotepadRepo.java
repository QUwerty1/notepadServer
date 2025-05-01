package org.quwerty.notepadserver.repositories;

import org.quwerty.notepadserver.entities.Notepad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotepadRepo extends JpaRepository<Notepad, Integer> {
    Optional<Notepad> findNotepadByName(String name);

    Optional<Notepad> findNotepadById(int id);
}

