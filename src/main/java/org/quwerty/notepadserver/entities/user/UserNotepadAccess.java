package org.quwerty.notepadserver.entities.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quwerty.notepadserver.entities.AccessType;
import org.quwerty.notepadserver.entities.Notepad;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user_notepad_access")
public class UserNotepadAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "notepad_id")
    private Notepad notepad;

    @Getter
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private AccessType accessType;

    public UserNotepadAccess(Notepad notepad, User user, org.quwerty.notepadserver.entities.AccessType accessType) {
        this.notepad = notepad;
        this.user = user;
        this.accessType = accessType;
    }
}
