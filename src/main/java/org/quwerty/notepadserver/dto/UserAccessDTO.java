package org.quwerty.notepadserver.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAccessDTO {
    int userId;
    AccessTypeDTO accessType;
}
