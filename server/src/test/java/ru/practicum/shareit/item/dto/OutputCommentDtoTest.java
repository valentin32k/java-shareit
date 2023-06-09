package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OutputCommentDtoTest {

    @Autowired
    private JacksonTester<OutputCommentDto> json;

    @Test
    void testOutputCommentDto() throws IOException {
        LocalDateTime created = LocalDateTime.now();
        OutputCommentDto dto = OutputCommentDto.builder()
                .id(23L)
                .text("Все понравилось")
                .authorName("Вася")
                .created(created)
                .build();
        JsonContent<OutputCommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
    }
}