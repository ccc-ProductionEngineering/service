package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.service.AuthService;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.data.Librarian;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void test_registerReader() throws Exception {
        when(authService.registerReader(any(Reader.class))).thenReturn("Reader registered successfully!");

        mockMvc.perform(post("/auth/register/reader")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Reader\",\"email\":\"reader@example.com\",\"password\":\"password\", \"role\":\"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reader registered successfully!"));
    }

    @Test
    void test_registerLibrarian() throws Exception {
        when(authService.registerLibrarian(any(Librarian.class))).thenReturn("Librarian registered successfully!");

        mockMvc.perform(post("/auth/register/librarian")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Librarian\",\"email\":\"librarian@example.com\",\"password\":\"password\", \"role\":\"ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Librarian registered successfully!"));
    }

    @Test
    void test_loginSuccess() throws Exception {
        when(authService.login("reader@example.com", "password")).thenReturn("valid-jwt-token");

        mockMvc.perform(post("/auth/login")
                .param("email", "reader@example.com")
                .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string("valid-jwt-token"));
    }

    @Test
    void test_loginFailure() throws Exception {
        when(authService.login("reader@example.com", "wrong-password")).thenReturn("Invalid email or password!");

        mockMvc.perform(post("/auth/login")
                .param("email", "reader@example.com")
                .param("password", "wrong-password"))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid email or password!"));
    }
}
