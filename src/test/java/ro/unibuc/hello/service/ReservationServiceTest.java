package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.data.Reservation;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.repository.ReaderRepository;
import ro.unibuc.hello.repository.ReservationRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_reserveBook_newReservation() {
        String bookId = "book1";
        String readerId = "reader1";

        when(reservationRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);

        int position = reservationService.reserveBook(bookId, readerId);

        verify(reservationRepository).save(captor.capture());
        Reservation saved = captor.getValue();

        assertEquals(1, position);
        assertTrue(saved.getReaderIds().contains(readerId));
        assertEquals(bookId, saved.getBookId());
    }

    @Test
    void test_reserveBook_existingReservation_newReader() {
        String bookId = "book1";
        String readerId = "reader1";

        Reservation reservation = new Reservation(bookId);
        when(reservationRepository.findByBookId(bookId)).thenReturn(Optional.of(reservation));

        int position = reservationService.reserveBook(bookId, readerId);

        verify(reservationRepository).save(reservation);
        assertEquals(1, position);
        assertTrue(reservation.getReaderIds().contains(readerId));
    }

    @Test
    void test_reserveBook_existingReader_notAddedAgain() {
        String bookId = "book1";
        String readerId = "reader1";

        Reservation reservation = new Reservation(bookId);
        reservation.addReader(readerId); // already added

        when(reservationRepository.findByBookId(bookId)).thenReturn(Optional.of(reservation));

        int position = reservationService.reserveBook(bookId, readerId);

        verify(reservationRepository, never()).save(any());
        assertEquals(1, position);
    }

    @Test
    void test_unreserveBook_found() {
        String bookId = "book1";
        String readerId = "reader1";

        Reservation reservation = new Reservation(bookId);
        reservation.addReader(readerId);

        when(reservationRepository.findByBookId(bookId)).thenReturn(Optional.of(reservation));

        boolean result = reservationService.unreserveBook(bookId, readerId);

        assertTrue(result);
        assertFalse(reservation.getReaderIds().contains(readerId));
        verify(reservationRepository).save(reservation);
    }

    @Test
    void test_unreserveBook_notFound() {
        String bookId = "book1";
        String readerId = "reader1";

        when(reservationRepository.findByBookId(bookId)).thenReturn(Optional.empty());

        boolean result = reservationService.unreserveBook(bookId, readerId);

        assertFalse(result);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void test_getReservationByBookId() {
        String bookId = "book1";
        Reservation reservation = new Reservation(bookId);

        when(reservationRepository.findByBookId(bookId)).thenReturn(Optional.of(reservation));

        Optional<Reservation> result = reservationService.getReservationByBookId(bookId);

        assertTrue(result.isPresent());
        assertEquals(reservation, result.get());
    }

    @Test
    void test_getReservationsByReaderId() {
        String readerId = "reader1";

        Reservation res1 = new Reservation("book1");
        res1.addReader(readerId);

        Reservation res2 = new Reservation("book2");
        res2.addReader("otherReader");

        when(reservationRepository.findAll()).thenReturn(Arrays.asList(res1, res2));

        List<Reservation> result = reservationService.getReservationsByReaderId(readerId);

        assertEquals(1, result.size());
        assertEquals("book1", result.get(0).getBookId());
    }

    @Test
    void test_getBookTitlesAndQueuePositionsByReaderId() {
        String readerId = "reader1";

        Reservation res = new Reservation("book1");
        res.addReader("reader2");
        res.addReader(readerId); // second in line

        Book book = new Book();
        book.setTitle("Test Book");

        when(reservationRepository.findAll()).thenReturn(List.of(res));
        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));

        List<Map<String, Object>> result = reservationService.getBookTitlesAndQueuePositionsByReaderId(readerId);

        assertEquals(1, result.size());
        Map<String, Object> bookInfo = result.get(0);
        assertEquals("Test Book", bookInfo.get("title"));
        assertEquals(2, bookInfo.get("queuePosition"));
    }

    @Test
    void test_getBookTitlesAndQueuePositionsByReaderId_bookNotFound() {
        String readerId = "reader1";

        Reservation res = new Reservation("book1");
        res.addReader(readerId);

        when(reservationRepository.findAll()).thenReturn(List.of(res));
        when(bookRepository.findById("book1")).thenReturn(Optional.empty());

        List<Map<String, Object>> result = reservationService.getBookTitlesAndQueuePositionsByReaderId(readerId);

        assertEquals(1, result.size());
        assertEquals("Unknown Title", result.get(0).get("title"));
    }
}
