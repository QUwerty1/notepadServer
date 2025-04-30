package org.quwerty.notepadserver.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotepadInfoDTO {
    int notepadId;
    String notepadName;
    Timestamp createdAt;
    Timestamp updatedAt;
    AccessTypeDTO accessType;
}
