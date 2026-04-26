package CodeBloom.AlquilaTusVehiculos.controllers;

import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import CodeBloom.AlquilaTusVehiculos.models.Rental;
import CodeBloom.AlquilaTusVehiculos.services.RentalService;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserProfileController {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    private final RentalService rentalService;

    public UserProfileController(UserRepository userRepository,
                                 VehicleRepository vehicleRepository,
                                 RentalService rentalService) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/rentals")
    public String myRentals(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("rentals", user.getRentals());
        return "user/rentals";
    }

    @GetMapping("/rentals/new")
    public String showNewRental(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Rental rental = new Rental();
        rental.setUser(user);
        model.addAttribute("rental", rental);
        model.addAttribute("users", List.of(user));
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "user/rental-new";   // ← vista separada
    }

    @PostMapping("/rentals/save")
    public String saveRental(@ModelAttribute Rental rental,
                             @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam("estimatedReturnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate estimatedReturnDate,
                             Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        rental.setUser(user);

        try {
            rentalService.saveRental(rental, startDate.atStartOfDay(), estimatedReturnDate.atStartOfDay());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", List.of(user));
            model.addAttribute("vehicles", vehicleRepository.findAll());
            return "rentals/create";
        }
        return "redirect:/user/rentals";
    }

    @InitBinder("rental")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("startDate", "estimatedReturnDate", "returnDate");
    }

    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute User user,
                              Authentication authentication) {
        String email = authentication.getName();
        User existing = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existing.setName(user.getName());
        existing.setPhone(user.getPhone());
        existing.setAddress(user.getAddress());
        existing.setDrivingLicense(user.getDrivingLicense());

        userRepository.save(existing);
        return "redirect:/user/profile";
    }

    @GetMapping("/vehicles")
    public String vehicles(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "user/vehicles";
    }

}