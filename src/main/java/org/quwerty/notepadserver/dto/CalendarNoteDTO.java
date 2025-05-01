package org.quwerty.notepadserver.dto;

import lombok.*;
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
