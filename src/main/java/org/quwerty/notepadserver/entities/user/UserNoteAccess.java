package org.quwerty.notepadserver.entities.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.note.Note;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "user_note_access")
public class UserNoteAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "access_type")
    @Enumerated(EnumType.STRING)
    AccessType accessType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Note note;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    User user;

    public UserNoteAccess(Note note, User user, AccessType accessType) {
        this.note = note;
        this.user = user;
        this.accessType = accessType;
    }
}
