package org.quwerty.notepadserver.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TextNoteDTO extends NoteDTO {
    String text;
}
