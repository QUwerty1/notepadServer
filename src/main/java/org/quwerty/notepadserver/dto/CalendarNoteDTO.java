package org.quwerty.notepadserver.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Map;

@Getter
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalendarNoteDTO extends NoteDTO {
    Map<Date, NoteCalendarEventDTO> calendarEvents;
}
