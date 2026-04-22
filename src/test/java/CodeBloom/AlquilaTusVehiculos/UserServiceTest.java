package CodeBloom.AlquilaTusVehiculos;

import org.springframework.security.crypto.password.PasswordEncoder;
import CodeBloom.AlquilaTusVehiculos.models.User;
import CodeBloom.AlquilaTusVehiculos.repositories.UserRepository;
import CodeBloom.AlquilaTusVehiculos.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldReturnUserWhenExists() {
        // 1. Preparación de usuario falso
        User user = new User();
        user.setId(1L);
        user.setName("Joan");

        // 2. Mandamos a Mock buscar el usuario con id 1 y lo devuelva
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 3. Llamamos al método real del servicio
        Optional<User> result = userService.getUserById(1L);

        // 4. Comprobamos que el resultado sea correcto
        assertTrue(result.isPresent());
        assertEquals("Joan", result.get().getName());
    }

    @Test
    public void shouldReturnAllUsers() {
        List<User> users = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    public void shouldSaveUser() {
        User user = new User();
        user.setName("Joan");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(user);

        assertEquals("Joan", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldUpdateUserWhenNotAdmin() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setIsAdmin(false);

        User userDetails = new User();
        userDetails.setName("Joana");
        userDetails.setPhone("123456789");
        userDetails.setAddress("Carrer Major 1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(1L, userDetails);

        assertEquals("Joana", result.getName());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void shouldNotUpdateUserWhenAdmin() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setIsAdmin(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.updateUser(1L, new User());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldResetPasswordWhenNotAdmin() {
        User user = new User();
        user.setId(1L);
        user.setIsAdmin(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("novaContrasenya")).thenReturn("hashedPassword");

        userService.resetPassword(1l, "novaContrasenya");

        assertEquals("hashedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldDeleteUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(99L, new User()));
    }
}
