package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = UserDtoMapper.class)
public interface ItemDtoMapper {

    @Mapping(target = "available", source = "isAvailable")
    ItemDto toItemDto(Item item);

    @Mapping(target = "isAvailable", source = "itemDto.available")
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    Item toItem(ItemDto itemDto, User owner);
}
