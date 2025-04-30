package org.quwerty.notepadserver.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.quwerty.notepadserver.domain.entity.user.User;
import org.quwerty.notepadserver.domain.entity.user.UserNotepadAccess;

import java.sql.Timestamp;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_notepad_access_id")
    private List<UserNotepadAccess> accessors;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owned_by_id")
    private User owner;

    public Notepad() {}
    public Notepad(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<UserNotepadAccess> getAccessors() {
        return accessors;
    }

    public void setAccessors(List<UserNotepadAccess> accessors) {
        this.accessors = accessors;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Notepad notepad) {
            return id == notepad.id;
        }
        return false;
    }
}
