package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.ForbiddenRequestException;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(Mapper dozerMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mapper = dozerMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationData registrationData) {
        String email = registrationData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        User user = mapper.map(registrationData, User.class);

        // TODO: 로직 변경하기
        user.changePassword(registrationData.getPassword(), passwordEncoder);

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserModificationData modificationData) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new ForbiddenRequestException());

        User source = mapper.map(modificationData, User.class);
        user.changeWith(source);
        user.changePassword(modificationData.getPassword(), passwordEncoder);

        return user;
    }

    public User deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.destroy();
        return user;
    }
}
