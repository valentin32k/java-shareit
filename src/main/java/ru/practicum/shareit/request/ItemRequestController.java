package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.InputItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.OutputItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public OutputItemRequestDto createItemRequest(@RequestBody @Valid InputItemRequestDto inputItemRequestDto,
                                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received POST /requests: '{}' for user with id = {}", inputItemRequestDto, userId);
        return ItemRequestMapper.toOutputItemRequestDto(
                itemRequestService.createItemRequest(
                        ItemRequestMapper.fromInputItemRequestDto(inputItemRequestDto),
                        userId));
    }

    @GetMapping
    public List<OutputItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received GET /requests: with userId = {}", userId);
        return ItemRequestMapper.toOutputItemRequestDtoList(
                itemRequestService
                        .getUserItemRequests(userId));
    }

    @GetMapping("/all")
    public List<OutputItemRequestDto> getItemRequests(@RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "20") int size,
                                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received GET /requests/all from = {}, size = {}, userId = {}", from, size, userId);
        return ItemRequestMapper.toOutputItemRequestDtoList(itemRequestService.getItemRequests(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public OutputItemRequestDto getItemRequestById(@PathVariable long requestId,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received GET /requests/{}", requestId);
        return ItemRequestMapper.toOutputItemRequestDto(
                itemRequestService
                        .getItemRequestById(requestId, userId));
    }
}