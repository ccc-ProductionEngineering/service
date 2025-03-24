package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.data.Reservation;
import ro.unibuc.hello.service.ReservationService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    void test_reserveBook_authenticated() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("reader1");

        when(reservationService.reserveBook("book1", "reader1")).thenReturn(2);

        mockMvc.perform(post("/reserve/book")
                .param("bookId", "book1")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(content().string("You are number 2 in the queue."));
    }

    @Test
    void test_reserveBook_unauthenticated() throws Exception {
        mockMvc.perform(post("/reserve/book")
                .param("bookId", "book1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authenticated."));
    }

    @Test
    void test_unreserveBook_success() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("reader1");

        when(reservationService.unreserveBook("book1", "reader1")).thenReturn(true);

        mockMvc.perform(post("/reserve/unreserve")
                .param("bookId", "book1")
                .principal(auth))
                .andExpect(status().isOk())
                .andExpect(content().string("Reservation cancelled."));
    }

    @Test
    void test_unreserveBook_failure() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("reader1");

        when(reservationService.unreserveBook("book1", "reader1")).thenReturn(false);

        mockMvc.perform(post("/reserve/unreserve")
                .param("bookId", "book1")
                .principal(auth))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No reservation found."));
    }

    @Test
    void test_getReservationList_found() throws Exception {
        Reservation reservation = new Reservation("book1");
        reservation.addReader("reader1");

        when(reservationService.getReservationByBookId("book1")).thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/reserve/list/book1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value("book1"))
                .andExpect(jsonPath("$.readerIds[0]").value("reader1"));
    }

    @Test
    void test_getReservationList_notFound() throws Exception {
        when(reservationService.getReservationByBookId("book1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/reserve/list/book1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No reservations found."));
    }

    @Test
    void test_getUserReservations_authenticated_withResults() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("reader1");

        List<Map<String, Object>> reservations = List.of(
                Map.of("title", "Book A", "queuePosition", 1),
                Map.of("title", "Book B", "queuePosition", 3)
        );

        when(reservationService.getBookTitlesAndQueuePositionsByReaderId("reader1"))
                .thenReturn(reservations);

        mockMvc.perform(get("/reserve/my-reservations").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book A"))
                .andExpect(jsonPath("$[0].queuePosition").value(1))
                .andExpect(jsonPath("$[1].title").value("Book B"))
                .andExpect(jsonPath("$[1].queuePosition").value(3));
    }

    @Test
    void test_getUserReservations_authenticated_noResults() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("reader1");

        when(reservationService.getBookTitlesAndQueuePositionsByReaderId("reader1"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reserve/my-reservations").principal(auth))
                .andExpect(status().isOk())
                .andExpect(content().string("No reservations found."));
    }

    @Test
    void test_getUserReservations_unauthenticated() throws Exception {
        mockMvc.perform(get("/reserve/my-reservations"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User not authenticated."));
    }
}
