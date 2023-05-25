package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatedUserDto {
    long id;
    @Size(max = 255, message = "Name must be shorter than 255 characters")
    String name;
    @Email(message = "The field email is incorrect ")
    @Size(max = 512, message = "Email must be shorter than 512 characters")
    String email;
}
