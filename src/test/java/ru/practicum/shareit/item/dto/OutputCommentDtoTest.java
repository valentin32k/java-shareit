package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OutputCommentDtoTest {

    @Autowired
    private JacksonTester<OutputCommentDto> json;

    @Test
    void testOutputCommentDto() throws IOException {
        LocalDateTime created = LocalDateTime.now();
        created = created.minusNanos(created.getNano() - 1100);
        OutputCommentDto dto = OutputCommentDto.builder()
                .id(23L)
                .text("Все понравилось")
                .authorName("Вася")
                .created(created)
                .build();
        JsonContent<OutputCommentDto> result = json.write(dto);
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        String createdStr = dto.getCreated().format(pattern);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(dto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(dto.getAuthorName());
        assertThat(result)
                .extractingJsonPathStringValue("$.created", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                .isEqualTo(createdStr);
    }
}