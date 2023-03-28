package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto addItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId, @RequestBody ItemDto itemDto) {
        log.debug("Received POST request to /items endpoint with userId={} and {}", ownerId, itemDto);
        return itemService.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.debug("Received PATCH request to /items/{} endpoint with userId={} and {}", itemId, ownerId, itemDto);
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.debug("Received GET request to /items endpoint with userId={}", userId);
        return itemService.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable Long itemId) {
        log.debug("Received GET request to /items/{} endpoint with userId={}", itemId, userId);
        return itemService.findById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        log.debug("Received GET request to /search endpoint with userId={} and text-param={}", userId, text);
        // todo search service
        return new ArrayList<>();
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.debug("Received DELETE request to /items/{} endpoint with userId={}", itemId, userId);
        itemService.delete(userId, itemId);
    }
 }

/* todo

    Добавление новой вещи. Будет происходить по эндпойнту POST /items.
    На вход поступает объект ItemDto. userId в заголовке X-Sharer-User-Id — это идентификатор пользователя,
    который добавляет вещь. Именно этот пользователь — владелец вещи.
    Идентификатор владельца будет поступать на вход в каждом из запросов, рассмотренных далее.

    Редактирование вещи. Эндпойнт PATCH /items/{itemId}. Изменить можно название, описание и статус доступа к аренде.
    Редактировать вещь может только её владелец.

    Просмотр информации о конкретной вещи по её идентификатору. Эндпойнт GET /items/{itemId}.
    Информацию о вещи может просмотреть любой пользователь.

    Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой. Эндпойнт GET /items.

    Поиск вещи потенциальным арендатором. Пользователь передаёт в строке запроса текст, и система ищет вещи,
    содержащие этот текст в названии или описании. Происходит по эндпойнту /items/search?text={text},
    в text передаётся текст для поиска. Проверьте, что поиск возвращает только доступные для аренды вещи.

    Для каждого из данных сценариев создайте соответственный метод в контроллере. Также создайте интерфейс ItemService
    и реализующий его класс ItemServiceImpl, к которому будет обращаться ваш контроллер.
    В качестве DAO создайте реализации, которые будут хранить данные в памяти приложения.
    Работу с базой данных вы реализуете в следующем спринте.

 */