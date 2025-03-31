package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.hello.NoSecurityConfig;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.data.Rent;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.repository.RentRepository;
import ro.unibuc.hello.repository.ReaderRepository;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(NoSecurityConfig.class)
public class RentControllerIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20");

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        String uri = mongoDBContainer.getReplicaSetUrl();
        registry.add("spring.data.mongodb.uri", () -> uri);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookRepository bookRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        rentRepository.deleteAll();
        readerRepository.deleteAll();
        bookRepository.deleteAll();

        Reader reader = new Reader();
        reader.setId("reader1");
        reader.setEmail("reader1@example.com");
        readerRepository.save(reader);

        Book book = new Book();
        book.setId("book1");
        book.setTitle("Test Book");
        book.setCopies(1); 
        bookRepository.save(book);
    }

    @Test
    void testRentBook_success() throws Exception {
        mockMvc.perform(post("/rent")
                        .param("bookId", "book1")
                        .with(user("reader1@example.com")))
                .andExpect(status().isCreated());
    }

    @Test
    void testRentBook_unauthenticated() throws Exception {
        mockMvc.perform(post("/rent")
                        .param("bookId", "book1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testReturnBook_success() throws Exception {
        Rent rent = new Rent();
        rent.setIdReader("reader1");
        rent.setIdBook("book1");
        rent.setBorrowDate(LocalDateTime.now());
        rentRepository.save(rent);

        mockMvc.perform(post("/return")
                        .param("bookId", "book1")
                        .with(user("reader1@example.com")))
                .andExpect(status().isOk());
    }

    @Test
    void testReturnBook_notFound() throws Exception {
        mockMvc.perform(post("/return")
                        .param("bookId", "book1")
                        .with(user("reader1@example.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testReturnBook_unauthenticated() throws Exception {
        mockMvc.perform(post("/return")
                        .param("bookId", "book1"))
                .andExpect(status().isUnauthorized());
    }
}