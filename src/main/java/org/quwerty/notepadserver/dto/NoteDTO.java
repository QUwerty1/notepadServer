package org.quwerty.notepadserver.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextNoteDTO.class, name = "Text"),
        @JsonSubTypes.Type(value = TableNoteDTO.class, name = "Table"),
        @JsonSubTypes.Type(value = CalendarNoteDTO.class, name = "Calendar")
})
public abstract class NoteDTO {
    int id;
    @NotNull
    String name;
    String type;
}
