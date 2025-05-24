package org.quwerty.notepadserver.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchNoteException extends RuntimeException {
    public NoSuchNoteException(String message) {
        super(message);
    }
}
