package org.quwerty.notepadserver.services;

import lombok.RequiredArgsConstructor;
import org.quwerty.notepadserver.dto.NotepadInfoDTO;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.note.Note;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNotepadAccess;
import org.quwerty.notepadserver.exceptions.ForbiddenException;
import org.quwerty.notepadserver.exceptions.NotepadAlreadyExistsException;
import org.quwerty.notepadserver.repositories.NotepadRepo;
import org.quwerty.notepadserver.repositories.UserNotepadAccessRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NotepadService {

    private final NotepadRepo notepadRepo;
    private final UserNotepadAccessRepo userNotepadAccessRepo;

    /**
     * @param notepad Блокнот, к которому проверяется доступ
     * @param user    Пользователь, чей доступ проверяется
     * @return Empty, если у пользователя нет доступа к блокноту, иначе, AccessType
     */
    public Optional<AccessType> getUserAccess(Notepad notepad, User user) {
        var access = notepad.getAccessors()
                .stream()
                .filter(au -> au.getUser() == user)
                .toList();

        if (access.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(access.getFirst().getAccessType());
        }
    }

    /**
     * @param name Название блокнота
     * @param user Владелец блокнота
     * @return Созданный блокнот
     * @throws NotepadAlreadyExistsException Блокнот с таким названием уже существует
     */
    public Notepad createNotepad(String name, User user) throws NotepadAlreadyExistsException {
        if (notepadRepo.findNotepadByName(name).isPresent()) {
            throw new NotepadAlreadyExistsException();
        } else {
            Notepad notepad = new Notepad(name, user);
            notepadRepo.save(notepad);
            return notepad;
        }
    }

    /**
     * @param notepad Блокнот, который пользователь собирается удалить
     * @param user    Пользователь, который собирается удалить блокнот
     * @throws ForbiddenException У пользователя недостаточно прав для удаления блокнота
     */
    public void deleteNotepad(Notepad notepad, User user) throws ForbiddenException {

        var optUserAccess = getUserAccess(notepad, user);

        if (optUserAccess.isPresent() && optUserAccess.get() == AccessType.Admin) {
            notepadRepo.delete(notepad);
        } else {
            throw new ForbiddenException();
        }
    }

    /**
     * @param notepad    Блокнот
     * @param accessType Тип доступа к блокноту пользователя, сделавшего запрос
     * @return NotepadInfoDTO
     */
    public NotepadInfoDTO toNotepadInfoDTO(Notepad notepad, AccessType accessType) {

        return new NotepadInfoDTO(
                notepad.getId(),
                notepad.getName(),
                notepad.getCreatedAt(),
                notepad.getUpdatedAt(),
                accessType.toString(),
                notepad.getNotes().stream().map(Note::getId).toList()
        );
    }

    /**
     * @param notepad    Блокнот, куда добавляется пользователь
     * @param actionist  Пользователь, который добавляет
     * @param subject    Пользователь, которого добавляют
     * @param accessType Тип доступа
     * @throws ForbiddenException subject уже добавлен или actionist
     *                            не является админом данного блокнота
     */
    public void addUserAccess(Notepad notepad, User actionist, User subject, AccessType accessType)
            throws ForbiddenException {
        var optActionistAccess = getUserAccess(notepad, actionist);
        if (getUserAccess(notepad, subject).isPresent() ||
                optActionistAccess.isPresent() &&
                        optActionistAccess.get() != AccessType.Admin) {

            throw new ForbiddenException();
        }
        notepad.getAccessors().add(new UserNotepadAccess(notepad, subject, accessType));
        notepadRepo.save(notepad);
    }

    /**
     * @param notepad    Блокнот, тип доступа к которому изменяется
     * @param actionist  Пользователь, изменяющий тип доступа
     * @param subject    Пользователь, чей тип доступа изменяют
     * @param accessType Тип доступа
     * @throws ForbiddenException subject не существует или actionist
     *                            не является админом данного блокнота
     */
    public void changeUserAccess(
            Notepad notepad,
            User actionist,
            User subject,
            AccessType accessType
    ) throws ForbiddenException {

        var optActionistAccess = getUserAccess(notepad, actionist);
        if (getUserAccess(notepad, subject).isEmpty() ||
                optActionistAccess.isPresent() &&
                        optActionistAccess.get() != AccessType.Admin) {

            throw new ForbiddenException();
        }
        UserNotepadAccess una = userNotepadAccessRepo
                .findByUserAndNotepad(subject, notepad)
                .orElseThrow(ForbiddenException::new);
        una.setAccessType(accessType);

        userNotepadAccessRepo.save(una);
    }

    /**
     * @param notepad   Блокнот
     * @param actionist Пользователь, который удаляет
     * @param subject   Пользователь, которого удаляют
     * @throws ForbiddenException actionist не является админом
     *                            данного блокнота или subject не существует
     */
    public void deleteUserFromNotepad(
            Notepad notepad,
            User actionist,
            User subject
    ) throws ForbiddenException {
        var optActionistAccess = getUserAccess(notepad, actionist);
        if (getUserAccess(notepad, subject).isEmpty() ||
                optActionistAccess.isPresent() &&
                        optActionistAccess.get() != AccessType.Admin) {

            throw new ForbiddenException();
        }
        userNotepadAccessRepo.delete(userNotepadAccessRepo
                .findByUserAndNotepad(subject, notepad)
                .orElseThrow(ForbiddenException::new)
        );
    }
}
