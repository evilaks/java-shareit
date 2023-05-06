package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentDtoMapperImplTest {

    private CommentDtoMapperImpl commentDtoMapper;

    @BeforeEach
    public void setUp() {
        commentDtoMapper = new CommentDtoMapperImpl();
    }

    @Test
    public void testToDto() {
        User author = new User();
        author.setId(1L);
        author.setName("User 1");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(author);
        comment.setText("Test comment");
        comment.setCreated(LocalDateTime.of(2023, 1, 1, 0, 0, 0));

        CommentDto commentDto = commentDtoMapper.toDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getAuthorName()).isEqualTo("User 1");
        assertThat(commentDto.getText()).isEqualTo("Test comment");
        assertThat(commentDto.getCreated()).isEqualTo("2023-01-01T00:00:00");
    }

    @Test
    public void testToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setAuthorName("User 1");
        commentDto.setText("Test comment");
        commentDto.setCreated("2023-01-01T00:00:00");

        Comment comment = commentDtoMapper.toComment(commentDto);

        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("Test comment");
        assertThat(comment.getCreated()).isEqualTo(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
    }
}
