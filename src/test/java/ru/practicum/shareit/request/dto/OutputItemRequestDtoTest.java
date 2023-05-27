package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OutputItemRequestDtoTest {

    @Autowired
    private JacksonTester<OutputItemRequestDto> json;

    @Test
    void testOutputItemRequestDto() throws IOException {
        LocalDateTime created = LocalDateTime.now().minusDays(11);
        created = created.minusNanos(created.getNano() - 1100);
        OutputItemRequestDto.ShortItemDto itemShort = OutputItemRequestDto.ShortItemDto.builder()
                .id(15L)
                .name("itemName")
                .description("itemDescr")
                .available(true)
                .requestId(11L)
                .build();
        OutputItemRequestDto dto = OutputItemRequestDto.builder()
                .id(44L)
                .description("requestDescr")
                .created(created)
                .items(List.of(itemShort))
                .build();
        JsonContent<OutputItemRequestDto> result = json.write(dto);
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        String createdStr = dto.getCreated().format(pattern);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result)
                .extractingJsonPathStringValue("$.created", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                .isEqualTo(createdStr);
        assertThat(result)
                .extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo((int) dto.getItems().get(0).getId());
        assertThat(result)
                .extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(dto.getItems().get(0).getName());
        assertThat(result)
                .extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(dto.getItems().get(0).getDescription());
        assertThat(result)
                .extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(dto.getItems().get(0).getAvailable());
        assertThat(result)
                .extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo((int) dto.getItems().get(0).getRequestId());
    }
}