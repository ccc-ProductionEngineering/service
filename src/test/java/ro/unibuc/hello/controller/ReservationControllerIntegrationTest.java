package ro.unibuc.hello.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ro.unibuc.hello.NoSecurityConfig;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.data.Reservation;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.repository.ReservationRepository;
import org.springframework.context.annotation.Import;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(NoSecurityConfig.class)
@Tag("IntegrationTest")
public class ReservationControllerIntegrationTest {

    @Container
    static final MongoDBContainer mongoDBContainer;

    static {
        mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
                .withCommand("--replSet", "rs0");
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
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void testGetReservationList_found() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        bookRepository.save(book);

        Reservation reservation = new Reservation(book.getId());
        reservation.addReader("reader1");
        reservationRepository.save(reservation);

        mockMvc.perform(get("/reserve/list/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(book.getId()))
                .andExpect(jsonPath("$.readerIds[0]").value("reader1"));
    }

    @Test
    void testGetReservationList_notFound() throws Exception {
        mockMvc.perform(get("/reserve/list/nonexistent"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No reservations found."));
    }

    @Test
    void testReserveBook() throws Exception {
        Book book = new Book();
        book.setTitle("To Reserve");
        bookRepository.save(book);

        mockMvc.perform(post("/reserve/book")
                        .param("bookId", book.getId())
                        .with(user("reader1")))
                .andExpect(status().isOk())
                .andExpect(content().string("You are number 1 in the queue."));
    }

    @Test
    void testUnreserveBook_success() throws Exception {
        Book book = new Book();
        book.setTitle("To Unreserve");
        bookRepository.save(book);

        Reservation reservation = new Reservation(book.getId());
        reservation.addReader("reader1");
        reservationRepository.save(reservation);

        mockMvc.perform(post("/reserve/unreserve")
                        .param("bookId", book.getId())
                        .with(user("reader1")))
                .andExpect(status().isOk())
                .andExpect(content().string("Reservation cancelled."));
    }

    @Test
    void testUnreserveBook_failure() throws Exception {
        Book book = new Book();
        book.setTitle("No Reservation");
        bookRepository.save(book);

        mockMvc.perform(post("/reserve/unreserve")
                        .param("bookId", book.getId())
                        .with(user("reader1")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No reservation found."));
    }

    @Test
    void testGetMyReservations_withResults() throws Exception {
        Book book = new Book();
        book.setTitle("Reserved Book");
        bookRepository.save(book);

        Reservation reservation = new Reservation(book.getId());
        reservation.addReader("reader1");
        reservationRepository.save(reservation);

        mockMvc.perform(get("/reserve/my-reservations")
                        .with(user("reader1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Reserved Book"))
                .andExpect(jsonPath("$[0].queuePosition").value(1));
    }

    @Test
    void testGetMyReservations_noResults() throws Exception {
        mockMvc.perform(get("/reserve/my-reservations")
                        .with(user("reader1")))
                .andExpect(status().isOk())
                .andExpect(content().string("No reservations found."));
    }

    @Test
    void testReserveBook_unauthenticated() throws Exception {
        Book book = new Book();
        book.setTitle("To Reserve");
        bookRepository.save(book);

        mockMvc.perform(post("/reserve/book")
                        .param("bookId", book.getId()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authenticated."));
    }
}
