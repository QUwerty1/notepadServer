package org.quwerty.notepadserver.entities.user;

import jakarta.persistence.*;
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
    @JoinColumn(name = "note_id")
    Note note;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    User user;

    public UserNoteAccess(Note note, User user, AccessType accessType) {
        this.note = note;
        this.user = user;
        this.accessType = accessType;
    }
}
