package org.quwerty.notepadserver.entities.note;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.quwerty.notepadserver.utils.calendar.CalendarEvent;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "calendar_notes")
public class CalendarNote extends Note {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "calendar_events")
    Map<Date, CalendarEvent> calendarEvents = new HashMap<>();
}
