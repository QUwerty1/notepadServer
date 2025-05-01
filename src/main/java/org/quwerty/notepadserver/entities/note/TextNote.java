package org.quwerty.notepadserver.entities.note;

import jakarta.persistence.Entity;
import lombok.*;
import org.quwerty.notepadserver.entities.Notepad;
import org.quwerty.notepadserver.entities.user.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "text_notes")
public class TextNote extends Note {
    private String text = "";

      public TextNote(Notepad notepad, String name, User owner) {
        super(notepad, name, owner);
    }
}
