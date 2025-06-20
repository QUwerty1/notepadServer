package org.quwerty.notepadserver.entities.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;

@NoArgsConstructor
@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_notepad_access")
public class UserNotepadAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "access_type")
    @Enumerated(EnumType.STRING)
    AccessType accessType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "notepad_id")
    Notepad notepad;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    User user;

    public UserNotepadAccess(Notepad notepad, User user, AccessType accessType) {
        this.notepad = notepad;
        this.user = user;
        this.accessType = accessType;
    }
}
