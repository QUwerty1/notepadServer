package org.quwerty.notepadserver.domain.entity.note;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.quwerty.notepadserver.domain.entity.Notepad;
import org.quwerty.notepadserver.domain.entity.user.User;
import org.quwerty.notepadserver.domain.entity.user.UserNoteAccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "notes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "Updated_at", nullable = false)
    private Timestamp updatedAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by_user_id")
    private User lockedBy;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notepad_id")
    private Notepad notepad;

    @Setter
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_note_access_id")
    private List<UserNoteAccess> accessors = new ArrayList<>();

    public Note(Notepad notepad, String name, User owner) {
        this.notepad = notepad;
        this.name = name;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Note note) {
            return id == note.id;
        }
        return false;
    }
}
