package org.quwerty.notepadserver.domain.entity.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.quwerty.notepadserver.domain.entity.Notepad;
import org.quwerty.notepadserver.domain.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "table_notes")
public class TableNote extends Note {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "column_names")
    private List<String> columnNames = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    private List<List<String>> cells = new ArrayList<>();

    public TableNote(Notepad notepad, String name, User owner) {
        super(notepad, name, owner);
    }
}

