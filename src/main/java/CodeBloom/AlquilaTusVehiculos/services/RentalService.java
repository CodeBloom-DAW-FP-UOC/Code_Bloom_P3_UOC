package CodeBloom.AlquilaTusVehiculos.services;

import CodeBloom.AlquilaTusVehiculos.models.Rental;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.models.Vehicle;
import CodeBloom.AlquilaTusVehiculos.repositories.RentalRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.repositories.VehicleRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class RentalService {
    private RentalRepository rentalRepository;
    private UserRepository userRepository;
    private VehicleRepository vehicleRepository;

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getAllEnabledRentals() {
        return rentalRepository.findByEnabledTrue();
    }

    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    public Rental saveRental(Rental newRental, LocalDateTime startDate, LocalDateTime estimatedReturnDate) {
        if (newRental.getUser() == null || newRental.getUser().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un cliente.");
        }

        if (newRental.getVehicle() == null || newRental.getVehicle().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un vehículo.");
        }

        if (startDate == null || estimatedReturnDate == null) {
            throw new IllegalArgumentException("Debes indicar ambas fechas.");
        }

        if (startDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de inicio de no puede ser anterior a hoy.");
        }

        if (estimatedReturnDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        User user = userRepository.findById(newRental.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente seleccionado no existe."));

        Vehicle vehicle = vehicleRepository.findById(newRental.getVehicle().getId())
                .orElseThrow(() -> new IllegalArgumentException("El vehículo seleccionado no existe."));

        newRental.setUser(user);
        newRental.setVehicle(vehicle);
        newRental.setStartDate(startDate);
        newRental.setEstimatedReturnDate(estimatedReturnDate);

        BigDecimal totalPrice = calculateTotalPrice(startDate, estimatedReturnDate, vehicle.getDailyPrice());
        newRental.setPrice(totalPrice.doubleValue());

        return rentalRepository.save(newRental);
    }


    public Rental updateRental(Long id, Rental rentalDetails) {
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found."));

        if (rental.getUser() == null || rental.getUser().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un cliente.");
        }

        if (rental.getVehicle() == null || rental.getVehicle().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un vehículo.");
        }

        if (rental.getStartDate() == null || rental.getEstimatedReturnDate() == null) {
            throw new IllegalArgumentException("Debes indicar ambas fechas.");
        }

        rental.setStartDate(rentalDetails.getStartDate());
        if (rental.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de inicio de no puede ser anterior a hoy.");
        }

        rental.setEstimatedReturnDate(rentalDetails.getEstimatedReturnDate());
        if (rental.getEstimatedReturnDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de devolución de no puede ser anterior a hoy.");
        }

        if (rental.getEstimatedReturnDate().isBefore(rental.getStartDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        BigDecimal newPrice = calculateTotalPrice(rentalDetails.getStartDate(), rentalDetails.getEstimatedReturnDate(), rentalDetails.getVehicle().getDailyPrice());
        rental.setPrice(newPrice.doubleValue());

        rental.setNote(rentalDetails.getNote());

        return rentalRepository.save(rental);
    }

    public void softDeleteVehicle(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found."));
        rental.setEnabled(false);
        rentalRepository.save(rental);
    }

    public void hardDeleteVehicle(Long id) {
        rentalRepository.deleteById(id);
    }

    private BigDecimal calculateTotalPrice(LocalDateTime startDate, LocalDateTime estimatedReturnDate, double dailyPrice) {
        long days = ChronoUnit.DAYS.between(startDate, estimatedReturnDate) + 1;
        return BigDecimal.valueOf(dailyPrice).multiply(BigDecimal.valueOf(days));
    }
}
