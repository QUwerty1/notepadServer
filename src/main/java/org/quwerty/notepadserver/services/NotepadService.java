package org.quwerty.notepadserver.services;

import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNotepadAccess;
import org.springframework.stereotype.Service;

@Service
public class NotepadService {
    public AccessType getUserAccess(Notepad notepad, User user) {
        return notepad.getAccessors()
                .stream()
                .filter(au -> au.getUser() == user)
                .toList()
                .getFirst()
                .getAccessType();
    }
}
