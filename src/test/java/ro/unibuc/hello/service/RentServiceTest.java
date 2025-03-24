package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.data.Rent;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.repository.ReaderRepository;
import ro.unibuc.hello.repository.RentRepository;
import ro.unibuc.hello.repository.ReservationRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

class RentServiceTest {

    @Mock
    private RentRepository rentRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private RentService rentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRentBook_Success() {
        String readerId = "reader123";
        String bookId = "book123";
        Reader reader = new Reader("John Doe", "reader@example.com", "password", "USER");
        Book book = new Book("Book Title", "Author Name", "Fiction", 5);
        Rent expectedRent = new Rent(readerId, bookId, LocalDateTime.now(), null);

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(rentRepository.save(any(Rent.class))).thenReturn(expectedRent);

        Rent rent = rentService.rentBook(readerId, bookId);

        assertNotNull(rent);
        assertEquals(readerId, rent.getIdReader());
        assertEquals(bookId, rent.getIdBook());
        verify(rentRepository, times(1)).save(any(Rent.class));
    }

    @Test
    void testRentBook_BookNotFound() {
        String readerId = "reader123";
        String bookId = "book123";
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> rentService.rentBook(readerId, bookId));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testRentBook_BookNotAvailable() {
        String readerId = "reader123";
        String bookId = "book123";
        Reader reader = new Reader("John Doe", "reader@example.com", "password", "USER");
        Book book = new Book("Book Title", "Author Name", "Fiction", 0);

        when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> rentService.rentBook(readerId, bookId));
        assertEquals("Book is not available for rent", exception.getMessage());
    }

    @Test
    void testReturnBook_Success() {
        String rentId = "rent123";
        Rent rent = new Rent("reader123", "book123", LocalDateTime.now().minusDays(2), null);
        Book book = new Book("Book Title", "Author Name", "Fiction", 6);

        when(rentRepository.findById(rentId)).thenReturn(Optional.of(rent));
        when(bookRepository.findById(rent.getIdBook())).thenReturn(Optional.of(book));
        when(rentRepository.save(any(Rent.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rent returnedRent = rentService.returnBook(rentId);

        assertNotNull(returnedRent);
        assertNotNull(returnedRent.getReturnDate());
        assertEquals(7, book.getCopies());
        verify(rentRepository, times(1)).save(returnedRent);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testReturnBook_BookAlreadyReturned() {
        String rentId = "rent123";
        Rent rent = new Rent("reader123", "book123", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        Book book = new Book("Book Title", "Author Name", "Fiction", 6);

        when(rentRepository.findById(rentId)).thenReturn(Optional.of(rent));
        when(bookRepository.findById(rent.getIdBook())).thenReturn(Optional.of(book));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            rentService.returnBook(rentId);
        });

        assertEquals("Book has already been returned.", exception.getMessage());
        verify(rentRepository, times(0)).save(any(Rent.class));
        verify(bookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void testReturnBook_RentNotFound() {
        String rentId = "rent123";
        when(rentRepository.findById(rentId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> rentService.returnBook(rentId));
        assertEquals("Rent not found with id rent123", exception.getMessage());
    }
}
