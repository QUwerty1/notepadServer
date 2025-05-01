package org.quwerty.notepadserver.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotepadInfoDTO {
    int notepadId;
    String notepadName;
    Timestamp createdAt;
    Timestamp updatedAt;
    String accessType;
    List<Integer> note_ids;
}
