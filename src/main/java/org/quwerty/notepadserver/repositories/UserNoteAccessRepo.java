package org.quwerty.notepadserver.repositories;

import org.quwerty.notepadserver.entities.note.Note;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNoteAccess;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserNoteAccessRepo extends CrudRepository<UserNoteAccess, Integer> {

    Optional<UserNoteAccess> findUserNoteAccessByUserAndNote(User user, Note note);
}
