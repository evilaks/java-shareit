package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    /*

    POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает,
    какая именно вещь ему нужна.

    GET /requests — получить список своих запросов вместе с данными об ответах на них.
    Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
    id вещи, название, id владельца. Так в дальнейшем, используя указанные id вещей, можно будет получить
    подробную информацию о каждой вещи. Запросы должны возвращаться в отсортированном порядке от более новых
    к более старым.

    GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
    С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
    Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
    Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size —
    количество элементов для отображения.

    GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах
    на него в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе
    может любой пользователь.

     */

    private final ItemRequestService itemRequestService;


    // POST /requests
    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Received POST-request at /requests endpoint with userId={}, itemRequestDto={}", userId, itemRequestDto);
        return ResponseEntity.ok().body(itemRequestService.createItemRequest(userId, itemRequestDto));
    }

    // GET /requests
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.debug("Received GET-request at /requests endpoint with userId={}", userId);
        return ResponseEntity.ok().body(itemRequestService.getUserItemRequests(userId));
    }

    // GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getOtherUsersItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                                          @RequestParam(defaultValue = "0") Integer from,
                                                                          @RequestParam(defaultValue = "100") Integer size) {
        log.debug("Received GET-request at /requests/all endpoint with from={}, size={}", from, size);
        return ResponseEntity.ok().body(itemRequestService.getOtherUsersItemRequests(userId, from, size));
    }

    // GET /requests/{requestId}
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long requestId) {
        log.debug("Received GET-request at /requests/ endpoint with requestId={}", requestId);
        return ResponseEntity.ok().body(itemRequestService.getItemRequestById(userId, requestId));
    }
}
