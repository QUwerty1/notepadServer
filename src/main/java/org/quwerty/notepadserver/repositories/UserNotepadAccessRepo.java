package org.quwerty.notepadserver.repositories;

import org.quwerty.notepadserver.entities.user.UserNotepadAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotepadAccessRepo extends JpaRepository<UserNotepadAccess, Integer> {
}
