package CodeBloom.AlquilaTusVehiculos.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    private String phone;
    private String address;

    private LocalDateTime registrationDate;
    private LocalDateTime modificationDate;

    @Column(unique = true)
    private String drivingLicense;

    private Boolean isAdmin;

    private boolean enabled = true;

    @OneToMany(mappedBy = "user")
    private List<Rental> rentals;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.registrationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modificationDate = LocalDateTime.now();
    }
}