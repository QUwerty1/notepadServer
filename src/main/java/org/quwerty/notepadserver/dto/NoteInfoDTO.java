package org.quwerty.notepadserver.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoteInfoDTO {
    int noteId;
    String noteName;
    Timestamp createdAt;
    Timestamp updatedAt;
    String accessType;
}
