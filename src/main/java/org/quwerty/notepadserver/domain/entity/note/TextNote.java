package org.quwerty.notepadserver.domain.entity.note;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quwerty.notepadserver.domain.entity.Notepad;
import org.quwerty.notepadserver.domain.entity.user.User;

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
