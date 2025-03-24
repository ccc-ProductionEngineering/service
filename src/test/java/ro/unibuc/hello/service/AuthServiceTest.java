package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.unibuc.hello.data.Librarian;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.repository.LibrarianRepository;
import ro.unibuc.hello.repository.ReaderRepository;
import ro.unibuc.hello.config.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private LibrarianRepository librarianRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_registerReader_Success() {
        Reader reader = new Reader("John Doe", "john@example.com", "password", "USER");
        when(readerRepository.existsByEmail(reader.getEmail())).thenReturn(false);
        when(librarianRepository.existsByEmail(reader.getEmail())).thenReturn(false);

        String response = authService.registerReader(reader);

        assertEquals("Reader registered successfully!", response);
        verify(readerRepository, times(1)).save(any(Reader.class));
    }

    @Test
    void test_registerReader_EmailAlreadyExists() {
        Reader reader = new Reader("John Doe", "john@example.com", "password", "USER");
        when(readerRepository.existsByEmail(reader.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.registerReader(reader));
        assertEquals("Email already exists!", exception.getMessage());
    }

    @Test
    void test_registerLibrarian_Success() {
        Librarian librarian = new Librarian("Jane Doe", "jane@example.com", "password", "ADMIN");
        when(readerRepository.existsByEmail(librarian.getEmail())).thenReturn(false);
        when(librarianRepository.existsByEmail(librarian.getEmail())).thenReturn(false);

        String response = authService.registerLibrarian(librarian);

        assertEquals("Librarian registered successfully!", response);
        verify(librarianRepository, times(1)).save(any(Librarian.class));
    }

    @Test
    void test_registerLibrarian_EmailAlreadyExists() {
        Librarian librarian = new Librarian("Jane Doe", "jane@example.com", "password", "ADMIN");
        when(readerRepository.existsByEmail(librarian.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.registerLibrarian(librarian));
        assertEquals("Email already exists!", exception.getMessage());
    }

    @Test
    void test_login_SuccessAsReader() {
        // Arrange
        String email = "reader@example.com";
        String password = "password";
        Reader reader = new Reader("Reader", email, "encoded-password", "USER");
        when(readerRepository.findByEmail(email)).thenReturn(Optional.of(reader));
        when(passwordEncoder.matches(password, reader.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email, "USER")).thenReturn("valid-jwt-token");

        String response = authService.login(email, password);

        assertEquals("valid-jwt-token", response);
    }

    @Test
    void test_login_SuccessAsLibrarian() {
        String email = "librarian@example.com";
        String password = "password";
        Librarian librarian = new Librarian("Librarian", email, "encoded-password", "ADMIN");
        when(librarianRepository.findByEmail(email)).thenReturn(Optional.of(librarian));
        when(passwordEncoder.matches(password, librarian.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(email, "ADMIN")).thenReturn("valid-jwt-token");

        String response = authService.login(email, password);

        assertEquals("valid-jwt-token", response);
    }

    @Test
    void test_login_InvalidEmailOrPassword() {
        String email = "nonexistent@example.com";
        String password = "wrong-password";
        when(readerRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(librarianRepository.findByEmail(email)).thenReturn(Optional.empty());

        String response = authService.login(email, password);

        assertEquals("Invalid email or password!", response);
    }
}
