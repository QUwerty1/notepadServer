package org.quwerty.notepadserver.repository;

import org.quwerty.notepadserver.domain.entity.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepo extends JpaRepository<Note, Integer> {}
