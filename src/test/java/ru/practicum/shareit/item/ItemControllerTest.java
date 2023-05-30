package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.OutputCommentDto;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mvc;
    private Item item;
    private OutputItemDto outputItemDto;
    private String json;

    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController controller;

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
        outputItemDto = OutputItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        json = mapper.writeValueAsString(outputItemDto);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(any(), anyLong(), anyLong())).thenReturn(item);

        mvc.perform(post("/items")
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(item);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        mvc.perform(get("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void getUserItems() throws Exception {
        json = mapper.writeValueAsString(List.of(outputItemDto));

        when(itemService.getUserItems(1, 0, 20)).thenReturn(List.of(item));

        mvc.perform(get("/items?from={from}&size={size}", 0, 20)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void findItemsWithText() throws Exception {
        json = mapper.writeValueAsString(List.of(outputItemDto));

        when(itemService.findItemsWithText("someText", 0, 20)).thenReturn(List.of(item));

        mvc.perform(get("/items/search?from={from}&size={size}", 0, 20)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "someText"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void createComment() throws Exception {
        User author = User.builder()
                .id(55)
                .name("Name")
                .email("e@m.r")
                .build();
        Comment comment = Comment.builder()
                .id(1)
                .text("someComment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
        OutputCommentDto outputCommentDto = OutputCommentDto.builder()
                .id(1)
                .text(comment.getText())
                .authorName(author.getName())
                .created(comment.getCreated())
                .build();
        json = mapper.writeValueAsString(outputCommentDto);

        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 55))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }
}