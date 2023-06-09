package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Value
public class UserDto {
    long id;
    @NotBlank(message = "The field name can not be blank")
    @Size(max = 255, message = "Name must be shorter than 255 characters")
    String name;
    @Email(message = "The field email is incorrect ")
    @NotEmpty(message = "The field email can not be empty")
    @Size(max = 512, message = "Email must be shorter than 512 characters")
    String email;
}
