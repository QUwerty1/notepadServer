package org.quwerty.notepadserver.entities.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Entity(name = "table_notes")
public class TableNote extends Note {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "column_names", nullable = false)
    List<String> columnNames;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cells", nullable = false)
    List<List<String>> cells;

    public TableNote(Notepad notepad, User user, String name) {
        super(notepad, user, name);
    }
}

