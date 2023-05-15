package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;

@Value
@Builder
public class UpdatedUserDto {
    long id;
    String name;
    @Email(message = "The field email is incorrect ")
    String email;
}
