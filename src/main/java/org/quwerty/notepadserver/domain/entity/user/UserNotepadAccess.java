package org.quwerty.notepadserver.domain.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.quwerty.notepadserver.domain.entity.Notepad;

@Getter
@Entity
@Table(name = "user_notepad_access")
public class UserNotepadAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notepad_id")
    private Notepad notepad;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private AccessType accessType;
}
