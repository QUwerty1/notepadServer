package org.quwerty.notepadserver.repositories;

import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNotepadAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotepadAccessRepo extends JpaRepository<UserNotepadAccess, Integer> {
    Optional<UserNotepadAccess> findByUserAndNotepad(User user, Notepad notepad);
}
