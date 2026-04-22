package CodeBloom.AlquilaTusVehiculos.Config;

import CodeBloom.AlquilaTusVehiculos.models.Rental;
import CodeBloom.AlquilaTusVehiculos.models.Role;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.models.Vehicle;
import CodeBloom.AlquilaTusVehiculos.repositories.RentalRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.RoleRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,
                      VehicleRepository vehicleRepository,
                      RentalRepository rentalRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

        if (userRepository.count() == 0) {
            User user1 = userRepository.save(User.builder()
                    .name("Joan Garcia")
                    .email("joan@example.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("1234567890")
                    .address("Carrer Major s/n")
                    .drivingLicense("B1234567")
                    .isAdmin(false)
                    .enabled(true)
                    .roles(Set.of(roleUser))
                    .build());

            User user2 = userRepository.save(User.builder()
                    .name("Maria López")
                    .email("maria@example.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("698765432")
                    .address("Avinguda Pau 5")
                    .drivingLicense("B7654321")
                    .isAdmin(false)
                    .enabled(true)
                    .roles(Set.of(roleUser))
                    .build());

            userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin"))
                    .phone("600000000")
                    .address("Carrer Admin 1")
                    .drivingLicense("B0000000")
                    .isAdmin(true)
                    .enabled(true)
                    .roles(Set.of(roleAdmin))
                    .build());

            Vehicle vehicle1 = vehicleRepository.save(Vehicle.builder()
                    .plateNumber("1234ABC")
                    .brand("Toyota")
                    .model("Corolla")
                    .manufacturingYear(2020)
                    .description("Coche familiar muy cómodo.")
                    .dailyPrice(45.0)
                    .gasType("Gasolina")
                    .km(30000)
                    .state("Disponible")
                    .build());

            Vehicle vehicle2 = vehicleRepository.save(Vehicle.builder()
                    .plateNumber("5678DEF")
                    .brand("Seat")
                    .model("Ibiza")
                    .manufacturingYear(2019)
                    .description("Coche compacto ideal para ciudad")
                    .dailyPrice(35.0)
                    .gasType("Diesel")
                    .km(50000)
                    .state("Disponible")
                    .build());

            rentalRepository.saveAll(List.of(
                    Rental.builder()
                            .startDate(LocalDateTime.now().minusDays(5))
                            .estimatedReturnDate(LocalDateTime.now().plusDays(2))
                            .price(BigDecimal.valueOf(315.0))
                            .note("Sin incidencias.")
                            .state("Actiu")
                            .user(user1)
                            .vehicle(vehicle1)
                            .build(),
                    Rental.builder()
                            .startDate(LocalDateTime.now().minusDays(10))
                            .estimatedReturnDate(LocalDateTime.now().minusDays(3))
                            .returnDate(LocalDateTime.now().minusDays(3))
                            .price(BigDecimal.valueOf(245.0))
                            .note("Devuelto con un golpe en la puerta.")
                            .state("Finalitzat")
                            .user(user2)
                            .vehicle(vehicle2)
                            .build()
            ));
        }
    }
}