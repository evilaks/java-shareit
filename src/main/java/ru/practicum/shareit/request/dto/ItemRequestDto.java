package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private String created;
    private List<ItemDto> items;
}
