package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OutputBookingDtoTest {
    @Autowired
    private JacksonTester<OutputBookingDto> json;

    @Test
    void testOutputBookingDto() throws IOException {
        OutputBookingDto.ShortItemDto item = new OutputBookingDto.ShortItemDto(22, "TV");
        OutputBookingDto.ShortUserDto user = new OutputBookingDto.ShortUserDto(300);
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(5);
        OutputBookingDto dto = OutputBookingDto.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .bookerId(66)
                .status(BookingStatus.WAITING)
                .build();
        JsonContent<OutputBookingDto> result = json.write(dto);
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String startTime = dto.getStart().format(pattern);
        String endTime = dto.getEnd().format(pattern);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) dto.getId());
        assertThat(result)
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(startTime);
        assertThat(result)
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(endTime);
        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo((int) dto.getItem().getId());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(dto.getItem().getName());
        assertThat(result).extractingJsonPathValue("$.booker.id").isEqualTo((int) dto.getBooker().getId());
        assertThat(result).extractingJsonPathValue("$.bookerId").isEqualTo((int) dto.getBookerId());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(dto.getStatus().toString());
    }
}