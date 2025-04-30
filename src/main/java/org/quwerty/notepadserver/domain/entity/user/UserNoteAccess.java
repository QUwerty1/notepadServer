package org.quwerty.notepadserver.domain.entity.user;

import jakarta.persistence.*;
import org.quwerty.notepadserver.domain.entity.note.Note;

@Entity
@Table(name = "user_note_access")
public class UserNoteAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private AccessType accessType;

    public int getId() {
        return id;
    }

    public Note getNote() {
        return note;
    }

    public User getUser() {
        return user;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }
}
