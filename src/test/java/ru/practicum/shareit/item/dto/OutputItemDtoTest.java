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
class OutputItemDtoTest {

    @Autowired
    private JacksonTester<OutputItemDto> json;

    @Test
    void testOutputItemDto() throws IOException {
        LocalDateTime nextStart = LocalDateTime.now().plusHours(35);
        nextStart = nextStart.minusNanos(nextStart.getNano() - 1100);
        LocalDateTime nextEnd = LocalDateTime.now().plusHours(37);
        nextEnd = nextEnd.minusNanos(nextEnd.getNano() - 1100);
        LocalDateTime lastStart = LocalDateTime.now().minusHours(35);
        lastStart = lastStart.minusNanos(lastStart.getNano() - 1100);
        LocalDateTime lastEnd = LocalDateTime.now().minusHours(37);
        lastEnd = lastEnd.minusNanos(lastEnd.getNano() - 1100);
        OutputItemDto.ShortBookingDto nextBooking = OutputItemDto.ShortBookingDto.builder()
                .id(1)
                .start(nextStart)
                .end(nextEnd)
                .bookerId(11L)
                .build();
        OutputItemDto.ShortBookingDto lastBooking = OutputItemDto.ShortBookingDto.builder()
                .id(1)
                .start(lastStart)
                .end(lastEnd)
                .bookerId(12L)
                .build();
        OutputItemDto dto = OutputItemDto.builder()
                .id(23L)
                .name("Яхта Eclipse")
                .description("Ну очень удобная яхта")
                .available(true)
                .nextBooking(nextBooking)
                .lastBooking(lastBooking)
                .requestId(344)
                .build();
        JsonContent<OutputItemDto> result = json.write(dto);
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        String nextBookingStartTime = dto.getNextBooking().getStart().format(pattern);
        String nextBookingEndTime = dto.getNextBooking().getEnd().format(pattern);
        String lastBookingStartTime = dto.getLastBooking().getStart().format(pattern);
        String lastBookingEndTime = dto.getLastBooking().getEnd().format(pattern);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result)
                .extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo((int) dto.getNextBooking().getId());
        assertThat(result)
                .extractingJsonPathStringValue("$.nextBooking.start", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                .isEqualTo(nextBookingStartTime);
        assertThat(result)
                .extractingJsonPathStringValue("$.nextBooking.end", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                .isEqualTo(nextBookingEndTime);
        assertThat(result)
                .extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(dto.getNextBooking().getBookerId().intValue());
        assertThat(result)
                .extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo((int) dto.getLastBooking().getId());
        assertThat(result)
                .extractingJsonPathStringValue("$.lastBooking.start", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                .isEqualTo(lastBookingStartTime);
        assertThat(result)
                .extractingJsonPathStringValue("$.lastBooking.end", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                .isEqualTo(lastBookingEndTime);
        assertThat(result)
                .extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(dto.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo((int) dto.getRequestId());
    }
}