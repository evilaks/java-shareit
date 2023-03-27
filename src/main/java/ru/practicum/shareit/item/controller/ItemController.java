package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto addItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId, @RequestBody ItemDto itemDto) {
        // todo add service
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        // todo update service
        return itemDto;
    }

    @GetMapping
    public List<ItemDto> findAllItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        // todo find all service
        return new ArrayList<>();
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                @PathVariable Long itemId) {
        // todo find by id service
        return null;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                     @RequestParam String text) {
        // todo search service
        return new ArrayList<>();
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