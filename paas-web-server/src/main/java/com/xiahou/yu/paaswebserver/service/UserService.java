package com.xiahou.yu.paaswebserver.service;

import com.xiahou.yu.paaswebserver.dto.input.CreateUserInput;
import com.xiahou.yu.paaswebserver.dto.input.UpdateUserInput;
import com.xiahou.yu.paaswebserver.entity.User;
import com.xiahou.yu.paaswebserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(CreateUserInput input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new RuntimeException("User with email " + input.getEmail() + " already exists");
        }

        User user = User.builder()
                .name(input.getName())
                .email(input.getEmail())
                .age(input.getAge())
                .build();

        return userRepository.save(user);
    }

    public User updateUser(Long id, UpdateUserInput input) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (input.getName() != null) {
            user.setName(input.getName());
        }
        if (input.getEmail() != null) {
            if (!user.getEmail().equals(input.getEmail()) && userRepository.existsByEmail(input.getEmail())) {
                throw new RuntimeException("User with email " + input.getEmail() + " already exists");
            }
            user.setEmail(input.getEmail());
        }
        if (input.getAge() != null) {
            user.setAge(input.getAge());
        }

        return userRepository.save(user);
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return true;
    }
}
