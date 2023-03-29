package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.exception.BadRequestException;
import ru.practicum.shareit.util.exception.ConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserDtoMapper userDtoMapper;

    @Override
    public UserDto findById(long userId) {
        if (this.doesExist(userId)) {
            return userDtoMapper.toUserDto(userStorage.findById(userId));
        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(userDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        if (this.isValidUser(userDto)) {
            if (this.haveNoConflicts(userDto)) {
                User userToAdd = userDtoMapper.toUser(userDto);
                return userDtoMapper.toUserDto(userStorage.add(userToAdd));
            } else throw new ConflictException("User has conflict with existing data");
        } else throw new BadRequestException("Invalid user object received");
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userToUpdate = userStorage.findById(userId);

        if (userToUpdate != null) {

                if (userDto.getEmail() != null) {
                    if (this.haveNoConflicts(userDto) || userToUpdate.getEmail().equals(userDto.getEmail())) {
                        if (this.isValidEmail(userDto.getEmail())) {
                            userToUpdate.setEmail(userDto.getEmail());
                        } else throw new BadRequestException("Invalid email");
                    } else throw new ConflictException("Email conflict found");
                }

                if (userDto.getName() != null) {
                    userToUpdate.setName(userDto.getName());
                }

                return userDtoMapper.toUserDto(userStorage.update(userId, userToUpdate));

        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    @Override
    public void delete(long userId) {
        if (this.doesExist(userId)) {
            userStorage.delete(userId);
        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    private boolean isValidUser(UserDto userDto) {

        // check if new user has proper username
        if (userDto.getName() == null || userDto.getName().isBlank() || userDto.getName().isEmpty()) {
            return false;
        }

        // check if new user has proper email
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || userDto.getName().isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean haveNoConflicts(UserDto userDto) {

        // check if email is already exist
        return userStorage.findAll()
                .stream()
                .map(User::getEmail)
                .noneMatch(email -> email.equals(userDto.getEmail()));
    }

    private boolean doesExist(Long userId) {
        return userStorage.findById(userId) != null;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

}
