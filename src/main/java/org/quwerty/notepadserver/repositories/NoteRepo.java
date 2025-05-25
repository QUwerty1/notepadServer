package org.quwerty.notepadserver.repositories;

import org.quwerty.notepadserver.dto.NoteDTO;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepo extends JpaRepository<Note, Integer> {
    public Optional<Note> findById(int id);

    List<Note> getByNotepad(Notepad notepad);
}
