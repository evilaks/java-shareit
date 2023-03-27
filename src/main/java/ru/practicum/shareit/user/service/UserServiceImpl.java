package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.exception.BadRequestException;
import ru.practicum.shareit.util.exception.ConflictException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User findById(long userId) {
        if (this.doesExist(userId)) {
            return userStorage.findById(userId);
        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    @Override
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User add(User user) {
        if (this.isValidUser(user)) {
            if (this.haveNoConflicts(user)) {
                return userStorage.add(user);
            } else throw new ConflictException("User has conflict with existing data");
        } else throw new BadRequestException("Invalid user object received");
    }

    @Override
    public User update(Long userId, User user) {
        User userToUpdate = userStorage.findById(userId);

        if (userToUpdate != null) {

                if (user.getEmail() != null) {
                    if (this.haveNoConflicts(user) || userToUpdate.getEmail().equals(user.getEmail())) {
                        if (this.isValidEmail(user.getEmail())) {
                            userToUpdate.setEmail(user.getEmail());
                        } else throw new BadRequestException("Invalid email");
                    } else throw new ConflictException("Email conflict found");
                }

                if (user.getName() != null) {
                    userToUpdate.setName(user.getName());
                }

                return userStorage.update(userId, userToUpdate);

        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    @Override
    public void delete(long userId) {
        if (this.doesExist(userId)) {
            userStorage.delete(userId);
        } else throw new NotFoundException("User with id=" + userId + " not found");
    }

    private boolean isValidUser(User user) {

        // check if new user has proper username
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            return false;
        }

        // check if new user has proper email
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getName().isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean haveNoConflicts(User user) {

        // check if email is already exist
        return userStorage.findAll()
                .stream()
                .map(User::getEmail)
                .noneMatch(email -> email.equals(user.getEmail()));
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
