package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.exception.BadRequestException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public UserDto findById(long userId) {
        if (this.doesExist(userId)) {
            return userDtoMapper.toUserDto(userRepository.findById(userId).orElse(null));
        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        if (this.isValidUser(userDto)) {
            User userToAdd = userDtoMapper.toUser(userDto);
            return userDtoMapper.toUserDto(userRepository.save(userToAdd));
        } else throw new BadRequestException("Invalid user object received");
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userToUpdate = userRepository.findById(userId).orElse(null);

        if (userToUpdate != null) {

                if (userDto.getEmail() != null) {
                    if (this.isValidEmail(userDto.getEmail())) {
                        userToUpdate.setEmail(userDto.getEmail());
                    } else throw new BadRequestException("Invalid email");
                }

                if (userDto.getName() != null) {
                    userToUpdate.setName(userDto.getName());
                }

                return userDtoMapper.toUserDto(userRepository.save(userToUpdate));

        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    @Transactional
    @Override
    public void delete(long userId) {
        if (this.doesExist(userId)) {
            userRepository.deleteById(userId);
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

    private boolean doesExist(Long userId) {
        return userRepository.findById(userId).orElse(null) != null;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

}
