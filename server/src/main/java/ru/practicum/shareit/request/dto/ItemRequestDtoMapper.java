package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = ItemDtoMapper.class)
public interface ItemRequestDtoMapper {
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items);

    @Mapping(target = "id", source = "itemRequestDto.id")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User author);
}
