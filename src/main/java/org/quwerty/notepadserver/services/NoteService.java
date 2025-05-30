package org.quwerty.notepadserver.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.quwerty.notepadserver.dto.*;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.note.*;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNoteAccess;
import org.quwerty.notepadserver.entities.user.UserNotepadAccess;
import org.quwerty.notepadserver.exceptions.ForbiddenException;
import org.quwerty.notepadserver.exceptions.NoSuchNoteException;
import org.quwerty.notepadserver.exceptions.NoSuchUserException;
import org.quwerty.notepadserver.exceptions.NoteAlreadyExistsException;
import org.quwerty.notepadserver.repositories.NoteRepo;
import org.quwerty.notepadserver.repositories.UserNoteAccessRepo;
import org.quwerty.notepadserver.repositories.UserNotepadAccessRepo;
import org.quwerty.notepadserver.repositories.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final UserNoteAccessRepo userNoteAccessRepo;
    private final NoteRepo noteRepo;
    private final UserNotepadAccessRepo userNotepadAccessRepo;
    private final UserRepo userRepo;

    /**
     * @param user Пользователь, который удаляет
     * @param note Заметка, который удаляют
     * @throws ForbiddenException У пользователя недостаточно прав для удаления
     */
    public void deleteNote(User user, Note note) throws ForbiddenException {
        Optional<UserNoteAccess> optUserAccess = userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(user, note);

        if (optUserAccess.isPresent()
                && optUserAccess.get().getAccessType() == AccessType.Admin) {
            noteRepo.delete(note);
        } else {
            throw new ForbiddenException();
        }
    }

    /**
     * Возвращает заметку, если она доступен пользователю
     *
     * @param noteId Id заметки
     * @param user   Пользователь
     * @return Заметка
     * @throws ForbiddenException У пользователя нет доступа к заметке
     */
    public Note findById(int noteId, User user) throws ForbiddenException {
        Optional<Note> optNote = noteRepo.findById(noteId);

        if (optNote.isPresent()) {
            Optional<UserNoteAccess> optUserAccess = userNoteAccessRepo
                    .findUserNoteAccessByUserAndNote(user, optNote.get());

            if (optUserAccess.isPresent()) {
                return optNote.get();

            } else {
                throw new ForbiddenException();
            }

        } else {
            throw new NoSuchNoteException();
        }
    }

    public NoteDTO toNoteDTO(Note note)
            throws NotImplementedException {

        NoteDTO noteDTO = switch (note) {
            case TextNote tn -> TextNoteDTO.builder()
                    .text(tn.getText())
                    .type(NoteType.Text.toString())
                    .build();
            case TableNote tn -> TableNoteDTO.builder()
                    .cells(tn.getCells())
                    .columnNames(tn.getColumnNames())
                    .type(NoteType.Table.toString())
                    .build();
            case CalendarNote cn -> CalendarNoteDTO.builder()
                    .calendarEvents(cn.getCalendarEvents())
                    .type(NoteType.Calendar.toString())
                    .build();
            default -> throw new NotImplementedException();
        };

        noteDTO.setId(note.getId());
        noteDTO.setName(note.getName());

        return noteDTO;
    }

    public Note toEntity(NoteDTO dto, Notepad notepad, User owner) {
        switch (dto) {
            case TextNoteDTO ignored:
                dto.setType(NoteType.Text.toString());
                break;
            case TableNoteDTO ignored:
                dto.setType(NoteType.Table.toString());
                break;
            case CalendarNoteDTO ignored:
                dto.setType(NoteType.Calendar.toString());
                break;
            default:
                throw new NotImplementedException();
        }

        return switch (dto) {
            case TextNoteDTO tn -> toTextNote(tn, notepad, owner);
            case TableNoteDTO tn -> toTableNote(tn, notepad, owner);
            case CalendarNoteDTO cn -> toCalendarNote(cn, notepad, owner);
            default -> throw new IllegalArgumentException("Unknown note type: " + dto.getType());
        };
    }

    private TextNote toTextNote(TextNoteDTO dto, Notepad notepad, User owner) {
        TextNote note = new TextNote(notepad, owner, dto.getName());
        note.setText(dto.getText());
        if (dto.getId() != 0) {
            note.setId(dto.getId());
        }
        return note;
    }

    private TableNote toTableNote(TableNoteDTO dto, Notepad notepad, User owner) {
        TableNote note = new TableNote(notepad, owner, dto.getName());
        note.setColumnNames(dto.getColumnNames());
        note.setCells(dto.getCells());
        if (dto.getId() != 0) {
            note.setId(dto.getId());
        }
        return note;
    }

    private CalendarNote toCalendarNote(CalendarNoteDTO dto, Notepad notepad, User owner) {
        CalendarNote note = new CalendarNote(notepad, owner, dto.getName());
        note.setCalendarEvents(dto.getCalendarEvents());
        if (dto.getId() != 0) {
            note.setId(dto.getId());
        }
        return note;
    }

    public void addNoteToNotepad(User user, NoteDTO noteDTO, Notepad notepad)
            throws NoteAlreadyExistsException, ForbiddenException {
        UserNotepadAccess notepadAccess = userNotepadAccessRepo
                .findByUserAndNotepad(user, notepad)
                .orElseThrow(ForbiddenException::new);

        if (noteRepo.findByName(noteDTO.getName()).isPresent()) {
            throw new NoteAlreadyExistsException();
        }

        if (notepadAccess.getAccessType() != AccessType.Reader) {
            Note note = toEntity(noteDTO, notepad, user);

            noteRepo.save(note);
        } else {
            throw new ForbiddenException();
        }
    }

    public void updateNote(User user, NoteDTO noteDTO)
            throws ForbiddenException, NoSuchNoteException, NoteAlreadyExistsException {

        Note origNote = noteRepo.findById(noteDTO.getId())
                .orElseThrow(NoSuchNoteException::new);
        Notepad notepad = origNote.getNotepad();

        Note note = toEntity(noteDTO, notepad, user);

        UserNotepadAccess notepadAccess = userNotepadAccessRepo
                .findByUserAndNotepad(user, notepad)
                .orElseThrow(ForbiddenException::new);

        UserNoteAccess noteAccess = userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(user, origNote)
                .orElseThrow(ForbiddenException::new);

        if (notepadAccess.getAccessType() != AccessType.Admin &&
                noteAccess.getAccessType() != AccessType.Admin) {

            throw new ForbiddenException();
        }

        if (noteRepo.findByNotepadAndName(notepad, noteDTO.getName()).isPresent()) {
            throw new NoteAlreadyExistsException();
        }

        if (origNote.getLockedBy() != user) {
            throw new ForbiddenException();
        }

        noteRepo.save(note);
    }

    public List<UserNoteAccess> getAccessorsByNote(User user, Note note)
            throws ForbiddenException {

        userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(user, note)
                .orElseThrow(ForbiddenException::new);

        return note.getAccessors();
    }

    public UserAccessDTO noteAccessToDTO(UserNoteAccess noteAccess) {
        return new UserAccessDTO(
                noteAccess.getUser().getId(),
                noteAccess.getAccessType().toString()
        );
    }

    public void addAccessor(User actor, UserNoteAccess noteAccess)
            throws ForbiddenException {

        UserNoteAccess actorNoteAccess = userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(actor, noteAccess.getNote())
                .orElseThrow(ForbiddenException::new);
        Optional<UserNoteAccess> subjectNoteAccess = userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(noteAccess.getUser(), noteAccess.getNote());

        if (actorNoteAccess.getAccessType() == AccessType.Admin && subjectNoteAccess.isEmpty()) {
            userNoteAccessRepo.save(noteAccess);

        } else {
            throw new ForbiddenException();
        }
    }

    public UserNoteAccess toEntity(UserAccessDTO userAccessDTO, Note note)
            throws NoSuchNoteException {

        return UserNoteAccess.builder()
                .user(userRepo.findById(userAccessDTO.getUserId())
                        .orElseThrow(NoSuchUserException::new))
                .note(note)
                .accessType(AccessType.valueOf(userAccessDTO.getAccessType()))
                .build();
    }

    public void changeAccessor(User actor, User subject, Note note, AccessType noteAccess)
            throws ForbiddenException {

        UserNoteAccess actorNoteAccess = userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(actor, note)
                .orElseThrow(ForbiddenException::new);

        if (actorNoteAccess.getAccessType() == AccessType.Admin) {

            UserNoteAccess subjectNoteAccess = userNoteAccessRepo
                    .findUserNoteAccessByUserAndNote(subject, note)
                    .orElseThrow(ForbiddenException::new);

            subjectNoteAccess.setAccessType(noteAccess);
            userNoteAccessRepo.save(subjectNoteAccess);

        } else {
            throw new ForbiddenException();
        }

    }

    public void lock(User user, Note note) throws ForbiddenException {
        UserNoteAccess noteAccess = userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(user, note)
                .orElseThrow(ForbiddenException::new);

        if (note.getLockedBy() == null || note.getLockedBy() == user
                && noteAccess.getAccessType() != AccessType.Reader) {

            note.setLockedBy(user);
            noteRepo.save(note);

        } else {
            throw new ForbiddenException();
        }
    }

    public void unlock(User user, Note note) throws ForbiddenException {
        userNoteAccessRepo
                .findUserNoteAccessByUserAndNote(user, note)
                .orElseThrow(ForbiddenException::new);

        if (note.getLockedBy() == user) {
            note.setLockedBy(null);
            noteRepo.save(note);

        } else {
            throw new ForbiddenException();
        }
    }
}
