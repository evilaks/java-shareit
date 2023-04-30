package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDto toDto(Comment comment);

    Comment toComment(CommentDto commentDto);
}
