package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto, Long ownerId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .ownerId(ownerId).build();
    }
}
