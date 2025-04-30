package org.quwerty.notepadserver.repository;

import org.quwerty.notepadserver.domain.entity.Notepad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotepadRepo extends JpaRepository<Notepad, Integer> {}
