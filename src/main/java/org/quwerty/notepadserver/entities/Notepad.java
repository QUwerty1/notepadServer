package org.quwerty.notepadserver.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.quwerty.notepadserver.entities.note.Note;
import org.quwerty.notepadserver.entities.user.User;
import org.quwerty.notepadserver.entities.user.UserNotepadAccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "notepads")
public class Notepad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "user_notepad_access_id")
    private List<UserNotepadAccess> accessors = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owned_by_id")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    public Notepad() {}
    public Notepad(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.accessors = new ArrayList<>();
        this.accessors.add(new UserNotepadAccess(this, owner, AccessType.Admin));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Notepad notepad) {
            return id == notepad.id;
        }
        return false;
    }
}
