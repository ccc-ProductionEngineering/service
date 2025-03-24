//failing tests: 401 UNAUTHORIZED

package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ro.unibuc.hello.data.Rent;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.service.RentService;
import ro.unibuc.hello.service.ReaderService;
import ro.unibuc.hello.service.BookService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class RentControllerTest {

    @Mock
    private RentService rentService;

    @Mock
    private ReaderService readerService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private RentController rentController;

    private Reader reader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reader = new Reader("John Doe", "john.doe@example.com", "password123", "USER");
    }

    @Test
    void testRentBook_Success() {
        when(readerService.getReaderByEmail("john.doe@example.com")).thenReturn(reader);
        Rent rent = new Rent("readerId", "bookId", LocalDateTime.now(), null);
        when(rentService.rentBook("readerId", "bookId")).thenReturn(rent);

        ResponseEntity<Rent> response = rentController.rentBook("bookId");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("readerId", response.getBody().getIdReader());
        assertEquals("bookId", response.getBody().getIdBook());
    }

    @Test
    void testRentBook_BookNotFound() {
        when(readerService.getReaderByEmail("john.doe@example.com")).thenReturn(reader);
        when(rentService.rentBook("readerId", "nonExistentBookId")).thenReturn(null);

        ResponseEntity<Rent> response = rentController.rentBook("nonExistentBookId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testReturnBook_Success() {
        when(readerService.getReaderByEmail("john.doe@example.com")).thenReturn(reader);
        Rent rent = new Rent("readerId", "bookId", LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        when(rentService.getRentsByBookIdAndReaderId("readerId", "bookId")).thenReturn(java.util.Optional.of(rent));
        when(rentService.returnBook("rentId")).thenReturn(rent);

        ResponseEntity<Rent> response = rentController.returnBook("bookId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("readerId", response.getBody().getIdReader());
    }

    @Test
    void testReturnBook_RentNotFound() {
        when(readerService.getReaderByEmail("john.doe@example.com")).thenReturn(reader);
        when(rentService.getRentsByBookIdAndReaderId("readerId", "nonExistentBookId")).thenReturn(java.util.Optional.empty());

        ResponseEntity<Rent> response = rentController.returnBook("nonExistentBookId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
