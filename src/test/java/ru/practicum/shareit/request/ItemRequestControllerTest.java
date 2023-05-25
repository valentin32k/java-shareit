package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private User user;
    private ItemRequest itemRequest;
    private InputItemRequestDto inputItemRequestDto;
    private OutputItemRequestDto outputItemRequestDto;
    @Mock
    ItemRequestService itemRequestService;
    @InjectMocks
    ItemRequestController controller;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        user = User.builder()
                .id(1)
                .email("john.doe@mail.com")
                .name("John")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .description("descr")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        inputItemRequestDto = InputItemRequestDto.builder()
                .description("descr")
                .build();
        outputItemRequestDto = ItemRequestMapper.toOutputItemRequestDto(itemRequest);

    }

    @Test
    void createItemRequest() throws Exception {
        String input = mapper.writeValueAsString(inputItemRequestDto);

        when(itemRequestService.createItemRequest(any(),anyLong())).thenReturn(itemRequest);

        mvc.perform(post("/requests", 1)
                        .content(input)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(outputItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items", is(outputItemRequestDto.getItems())));

    }

//    @Test
//    void getUserItemRequests() {
//    }
//
//    @Test
//    void getItemRequests() {
//    }
//
//    @Test
//    void getItemRequestById() {
//    }
}