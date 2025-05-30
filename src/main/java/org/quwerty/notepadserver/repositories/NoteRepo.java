package org.quwerty.notepadserver.repositories;

import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepo extends JpaRepository<Note, Integer> {
    Optional<Note> findById(int id);

    Optional<Note> findByName(String name);

    Optional<Note> findByNotepadAndName(Notepad notepad, String name);
}
