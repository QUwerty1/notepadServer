package org.quwerty.notepadserver.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.quwerty.notepadserver.utils.calendar.CalendarEvent;

import java.sql.Date;
import java.util.Map;

@SuperBuilder
@Getter
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalendarNoteDTO extends NoteDTO {
    Map<Date, CalendarEvent> calendarEvents;
}
