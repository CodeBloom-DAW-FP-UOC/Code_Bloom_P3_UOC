package CodeBloom.AlquilaTusVehiculos.services;

import CodeBloom.AlquilaTusVehiculos.Config.PasswordUtils;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));

        if (!user.getIsAdmin()) {
            user.setName(userDetails.getName());
            user.setPhone(userDetails.getPhone());
            user.setAddress(userDetails.getAddress());
            return userRepository.save(user);
        } else {
            return user;
        }
    }

    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));

        if (!user.getIsAdmin()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));
        userRepository.delete(user);
    }
}
