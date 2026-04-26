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
import java.util.Set;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login/login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Ya existe una cuenta con ese email.");
            return "auth/register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return "redirect:/login";
    }
}