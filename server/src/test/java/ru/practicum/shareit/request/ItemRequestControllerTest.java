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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private ItemRequest itemRequest;
    private OutputItemRequestDto outputItemRequestDto;
    private String json;
    @Mock
    ItemRequestService itemRequestService;
    @InjectMocks
    ItemRequestController controller;

    @BeforeEach
    void setup() throws JsonProcessingException {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        User user = User.builder()
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
        InputItemRequestDto inputItemRequestDto = new InputItemRequestDto(1, "descr");
        outputItemRequestDto = ItemRequestMapper.toOutputItemRequestDto(itemRequest);
        json = mapper.writeValueAsString(inputItemRequestDto);
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(), anyLong())).thenReturn(itemRequest);

        mvc.perform(post("/requests", 1)
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outputItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(outputItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items", is(outputItemRequestDto.getItems())));
    }

    @Test
    void getUserItemRequests() throws Exception {
        json = mapper.writeValueAsString(List.of(outputItemRequestDto));

        when(itemRequestService.getUserItemRequests(anyLong())).thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getItemRequests() throws Exception {
        String json = mapper.writeValueAsString(List.of(outputItemRequestDto));

        when(itemRequestService.getItemRequests(anyInt(), anyInt(), anyLong())).thenReturn(List.of(itemRequest));

        mvc.perform(get("/requests/all?from={from}&size={size}", 0, 20)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequest);

        mvc.perform(get("/requests/{requestId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }
}