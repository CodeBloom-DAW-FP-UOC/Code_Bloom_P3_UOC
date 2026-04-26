package CodeBloom.AlquilaTusVehiculos.controllers;

import CodeBloom.AlquilaTusVehiculos.models.Rental;
import CodeBloom.AlquilaTusVehiculos.services.RentalService;
import CodeBloom.AlquilaTusVehiculos.services.UserService;
import CodeBloom.AlquilaTusVehiculos.services.VehicleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    private final RentalService rentalService;
    private final UserService userService;
    private final VehicleService vehicleService;

    public AdminRentalController(RentalService rentalService,
                                 UserService userService,
                                 VehicleService vehicleService) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String listRentals(Model model) {
        model.addAttribute("rentals", rentalService.getAllRentals());
        return "rentals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("rental", new Rental());
        loadFormData(model);
        return "rentals/create";
    }

    @PostMapping("/save")
    public String saveRental(@ModelAttribute Rental rental,
                             @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam("estimatedReturnDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate estimatedReturnDate,
                             Model model) {
        try {
            rentalService.saveRental(rental, startDate.atStartOfDay(), estimatedReturnDate.atStartOfDay());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            loadFormData(model, rental);
            return "rentals/create";
        }
        return "redirect:/admin/rentals";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Rental rental = rentalService.getRentalById(id).orElse(null);
        if (rental == null) return "redirect:/admin/rentals";
        model.addAttribute("rental", rental);
        loadFormData(model);
        return "rentals/create";
    }

    @GetMapping("/delete/{id}")
    public String deleteRental(@PathVariable Long id) {
        rentalService.getRentalById(id).ifPresent(rental -> {
            rental.setUser(null);
            rental.setVehicle(null);
            rentalService.hardDeleteRental(rental.getId());
        });
        return "redirect:/admin/rentals";
    }

    private void loadFormData(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        model.addAttribute("rentals", rentalService.getAllRentals());
    }

    private void loadFormData(Model model, Rental rental) {
        model.addAttribute("rental", rental);
        loadFormData(model);
    }

    @InitBinder("rental")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("startDate", "estimatedReturnDate", "returnDate");
    }
}