package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapperImpl;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserDtoMapperImplTest {

    @Autowired
    private UserDtoMapperImpl userDtoMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User 1");
        user.setEmail("user1@example.com");

        userDto = UserDto.builder()
                .id(1L)
                .name("User 1")
                .email("user1@example.com")
                .build();
    }

    @Test
    public void testToUserDto() {
        UserDto result = userDtoMapper.toUserDto(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testToUser() {
        User result = userDtoMapper.toUser(userDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userDto.getId());
        assertThat(result.getName()).isEqualTo(userDto.getName());
        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());
    }
}
