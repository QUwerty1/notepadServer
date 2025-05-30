package org.quwerty.notepadserver.entities.note;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNoteAccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false, unique = true)
    String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    User owner;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by_id")
    User lockedBy;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "notepad_id")
    Notepad notepad;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    List<UserNoteAccess> accessors = new ArrayList<>();

    public Note(Notepad notepad, User owner, String name) {
        this.name = name;
        this.notepad = notepad;
        this.owner = owner;
        this.accessors = List.of(new UserNoteAccess(this, owner, AccessType.Admin));
    }
}
