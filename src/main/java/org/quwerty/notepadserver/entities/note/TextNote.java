package org.quwerty.notepadserver.entities.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "text_notes")
public class TextNote extends Note {
    @Column(nullable = false)
    String text;

    public TextNote(Notepad notepad, User user, String name) {
        super(notepad, user, name);
    }
}
