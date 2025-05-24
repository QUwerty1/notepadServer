package org.quwerty.notepadserver.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.quwerty.notepadserver.dto.CalendarNoteDTO;
import org.quwerty.notepadserver.dto.NoteDTO;
import org.quwerty.notepadserver.dto.TableNoteDTO;
import org.quwerty.notepadserver.dto.TextNoteDTO;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.note.*;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNoteAccess;
import org.quwerty.notepadserver.exceptions.ForbiddenException;
import org.quwerty.notepadserver.exceptions.NoSuchNoteException;
import org.quwerty.notepadserver.repositories.NoteRepo;
import org.quwerty.notepadserver.repositories.UserNoteAccessRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class NoteService {
    private final UserNoteAccessRepo userNoteAccessRepo;
    private final NoteRepo noteRepo;

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
}
