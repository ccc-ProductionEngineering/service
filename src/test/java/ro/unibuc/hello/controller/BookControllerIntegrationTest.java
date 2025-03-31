
package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.hello.NoSecurityConfig;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.BookRepository;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(NoSecurityConfig.class)
@Tag("IntegrationTest")
public class BookControllerIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer;

    static {
        mongoDBContainer = new MongoDBContainer("mongo:6.0.20");
        mongoDBContainer.start(); 
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        String uri = mongoDBContainer.getReplicaSetUrl();
        registry.add("mongodb.connection.url", () -> uri);
        registry.add("spring.data.mongodb.uri", () -> uri);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDb() {
        bookRepository.deleteAll();
    }
    @Test
    void testAddBookAndGetAllBooks() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("John Doe");

        mockMvc.perform(post("/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cartea a fost adăugată cu succes!"));

        mockMvc.perform(get("/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Test Book")))
                .andExpect(jsonPath("$[*].author", hasItem("John Doe")));
    }

    @Test
    void testUpdateBook_success() throws Exception {
        Book book = new Book();
        book.setTitle("Initial Title");
        book.setAuthor("Author A");
        Book savedBook = bookRepository.save(book);

        Book updatedBook = new Book();
        updatedBook.setTitle("Updated Title");
        updatedBook.setAuthor("Author B");

        mockMvc.perform(put("/books/update/" + savedBook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cartea a fost actualizata cu succes!"));
    }

    @Test
    void testUpdateBook_notFound() throws Exception {
        Book updatedBook = new Book();
        updatedBook.setTitle("Not Found");
        updatedBook.setAuthor("Nobody");

        mockMvc.perform(put("/books/update/invalid-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cartea nu a fost gasita!"));
    }

    @Test
    void testDeleteBook_success() throws Exception {
        Book book = new Book();
        book.setTitle("To be deleted");
        book.setAuthor("Author Z");
        Book savedBook = bookRepository.save(book);

        mockMvc.perform(delete("/books/delete/" + savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Cartea a fost stearsa cu succes!"));
    }

    @Test
    void testDeleteBook_notFound() throws Exception {
        mockMvc.perform(delete("/books/delete/invalid-id"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cartea nu a fost gasita!"));
    }
}
