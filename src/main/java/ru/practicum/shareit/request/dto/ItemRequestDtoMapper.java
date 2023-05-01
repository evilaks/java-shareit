package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemRequestDtoMapper {
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "id", source = "itemRequestDto.id")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User author);
}
