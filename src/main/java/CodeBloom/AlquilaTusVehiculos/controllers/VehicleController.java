package CodeBloom.AlquilaTusVehiculos.controllers;

import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "vehicles/list";
    }
}