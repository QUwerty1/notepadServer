package org.quwerty.notepadserver.domain.entity.note;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.quwerty.notepadserver.domain.entity.Notepad;
import org.quwerty.notepadserver.domain.entity.note.calendar.CalendarEvent;
import org.quwerty.notepadserver.domain.entity.user.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
