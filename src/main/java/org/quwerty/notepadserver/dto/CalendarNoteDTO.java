package org.quwerty.notepadserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.quwerty.notepadserver.utils.calendar.CalendarEvent;

import java.sql.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalendarNoteDTO extends NoteDTO {
    @NotNull
    Map<Date, CalendarEvent> calendarEvents;

}
