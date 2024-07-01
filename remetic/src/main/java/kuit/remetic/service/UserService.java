package kuit.remetic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kuit.remetic.model.User;
import kuit.remetic.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }


}