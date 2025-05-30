package org.quwerty.notepadserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TableNoteDTO extends NoteDTO {
    @NotNull
    List<String> columnNames;
    @NotNull
    List<List<String>> cells;

}
