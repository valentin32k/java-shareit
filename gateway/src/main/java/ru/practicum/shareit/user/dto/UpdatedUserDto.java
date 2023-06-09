package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Value
public class UpdatedUserDto {
    long id;
    @Size(max = 255, message = "Name must be shorter than 255 characters")
    String name;
    @Email(message = "The field email is incorrect ")
    @Size(max = 512, message = "Email must be shorter than 512 characters")
    String email;
}
