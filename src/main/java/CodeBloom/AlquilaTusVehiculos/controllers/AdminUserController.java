package CodeBloom.AlquilaTusVehiculos.controllers;

import CodeBloom.AlquilaTusVehiculos.models.Role;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.RoleRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", new User());
        return "users/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) return "redirect:/admin/users";
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", user.get());
        return "users/users";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        if (user.getId() == null) {
            // Usuario nuevo: encriptar contraseña y asignar ROLE_USER
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        } else {
            // Usuario existente: mantener contraseña y roles anteriores
            Optional<User> existing = userRepository.findById(user.getId());
            existing.ifPresent(e -> {
                user.setPassword(e.getPassword());
                user.setRoles(e.getRoles());
                user.setEnabled(e.isEnabled());
            });
        }
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User u = user.get();
            u.getRentals().forEach(r -> r.setUser(null));
            u.getRoles().clear();
            userRepository.save(u);
            userRepository.deleteById(id);
        }
        return "redirect:/admin/users";
    }
}