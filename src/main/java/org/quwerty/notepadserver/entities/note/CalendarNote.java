package org.quwerty.notepadserver.entities.note;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.note.calendar.CalendarEvent;
import org.quwerty.notepadserver.entities.user.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "calendar_notes")
public class CalendarNote extends Note {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "calendar_events")
    private Map<Date, CalendarEvent> calendarEvents = new HashMap<>();

    public CalendarNote(Notepad notepad, String name, User owner) {
        super(notepad, name, owner);
    }
}
