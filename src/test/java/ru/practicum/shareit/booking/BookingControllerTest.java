package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private Item item;
    private User booker;
    private Booking booking;
    private OutputBookingDto outputBookingDto;
    private String json;

    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController controller;

    @BeforeEach
    void setup() throws JsonProcessingException {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        item = Item.builder()
                .id(1)
                .name("name")
                .description("descr")
                .available(true)
                .build();
        booker = User.builder()
                .id(55)
                .name("Name")
                .email("e@m.r")
                .build();
        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(15))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        outputBookingDto = OutputBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new OutputBookingDto.ShortItemDto(booking.getItem().getId(), booking.getItem().getName()))
                .booker(new OutputBookingDto.ShortUserDto(booking.getBooker().getId()))
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
        json = mapper.writeValueAsString(outputBookingDto);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any())).thenReturn(booking);
        when(userService.getUserById(anyLong())).thenReturn(booker);
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        mvc.perform(post("/bookings")
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 55))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void confirmBooking() throws Exception {
        when(bookingService.confirmBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(true))
                        .header("X-Sharer-User-Id", 55))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 55))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getBookingsByUserId() throws Exception {
        json = mapper.writeValueAsString(List.of(outputBookingDto));

        when(bookingService.getBookingsByUserId(any(), anyInt(), anyInt(), anyLong())).thenReturn(List.of(booking));

        mvc.perform(get("/bookings?state={state}&from={from}&size={size}", "ALL", 0, 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 55))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getOwnerItemsBookings() throws Exception {
        json = mapper.writeValueAsString(List.of(outputBookingDto));

        when(bookingService.getOwnerItemsBookings(any(), anyInt(), anyInt(), anyLong())).thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "ALL", 0, 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 55))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }
}