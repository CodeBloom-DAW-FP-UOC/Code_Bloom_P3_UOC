package CodeBloom.AlquilaTusVehiculos.controllers;

import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.RentalRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public DashboardController(UserRepository userRepository,
                               VehicleRepository vehicleRepository,
                               RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            model.addAttribute("totalUsers", userRepository.count());
            model.addAttribute("totalVehicles", vehicleRepository.count());
            model.addAttribute("totalRentals", rentalRepository.count());
            return "dashboard/dashboard";
        } else {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            model.addAttribute("user", user);
            model.addAttribute("totalRentals", user.getRentals().size());
            model.addAttribute("totalVehicles", vehicleRepository.count());
            return "dashboard/dashboard-user";
        }
    }

    @GetMapping("/error/403")
    public String error403() {
        return "error/403";
    }
}