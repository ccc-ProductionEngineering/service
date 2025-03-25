package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import ro.unibuc.hello.data.Rent;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.service.RentService;
import ro.unibuc.hello.service.ReaderService;
import ro.unibuc.hello.service.BookService;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RentControllerTest {

    @Mock
    private RentService rentService;

    @Mock
    private ReaderService readerService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private RentController rentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rentController).build();
    }

    @Test
    void test_rentBook_authenticated() throws Exception {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        
        when(userDetails.getUsername()).thenReturn("reader1@example.com");
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(auth.getName()).thenReturn("reader1@example.com");

        Reader mockReader = new Reader("Reader Name", "reader1@example.com", "password", "USER");
        mockReader.setId("reader1-id");

        when(readerService.getReaderByEmail("reader1@example.com")).thenReturn(mockReader);

        Rent mockRent = new Rent("reader1-id", "book1", LocalDateTime.now(), null);
        when(rentService.rentBook("reader1-id", "book1")).thenReturn(mockRent);

        mockMvc.perform(post("/rent")
                .param("bookId", "book1")
                .principal(auth))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReader").value("reader1-id"))
                .andExpect(jsonPath("$.idBook").value("book1"))
                .andExpect(jsonPath("$.borrowDate").exists());
    }

    @Test
    void test_rentBook_unauthenticated() throws Exception {
        mockMvc.perform(post("/rent")
                .param("bookId", "bookId"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test_returnBook_authenticated() throws Exception {
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn("reader1@example.com");
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(auth.getName()).thenReturn("reader1@example.com");

        Reader mockReader = new Reader("Reader Name", "reader1@example.com", "password", "USER");
        mockReader.setId("reader1-id");

        when(readerService.getReaderByEmail("reader1@example.com")).thenReturn(mockReader);

        Rent mockRent = new Rent("reader1-id", "book1", LocalDateTime.now().minusDays(2), null);
        when(rentService.getRentsByBookIdAndReaderId("reader1-id", "book1")).thenReturn(Optional.of(mockRent));

        Rent returnedRent = new Rent("reader1-id", "book1", LocalDateTime.now().minusDays(2), LocalDateTime.now());
        when(rentService.returnBook(mockRent.getId())).thenReturn(returnedRent);

        mockMvc.perform(post("/return")
                .param("bookId", "book1")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReader").value("reader1-id"))
                .andExpect(jsonPath("$.idBook").value("book1"))
                .andExpect(jsonPath("$.returnDate").exists());
    }

    @Test
    void test_returnBook_Unauthorized() throws Exception {
        mockMvc.perform(post("/return")
               .param("bookId", "1"))
               .andExpect(status().isUnauthorized());
    }
}
