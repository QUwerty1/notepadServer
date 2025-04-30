package org.quwerty.notepadserver.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TableNoteDTO extends NoteDTO {
    List<String> columnNames;
    List<List<String>> cell;
}
