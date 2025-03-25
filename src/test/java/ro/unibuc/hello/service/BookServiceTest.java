
package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.BookRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookService();
        bookService.bookRepository = bookRepository;
    }

    @Test
    public void testAddBook() {
        Book book = new Book("Title", "Author", "Genre", 3);
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.addBook(book);
        assertEquals("Title", result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testGetAllBooks() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetByAvailableTitle_found() {
        Book book = new Book("Title", "Author", "Genre", 1);
        when(bookRepository.findByTitle("Title")).thenReturn(List.of(book));

        Optional<Book> result = bookService.getByAvailableTitle("Title");
        assertTrue(result.isPresent());
    }

    @Test
    public void testGetByAvailableTitle_notFound() {
        Book book = new Book("Title", "Author", "Genre", 0);
        when(bookRepository.findByTitle("Title")).thenReturn(List.of(book));

        Optional<Book> result = bookService.getByAvailableTitle("Title");
        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateBook_success() {
        Book existing = new Book("Old", "Old Author", "Old Genre", 1);
        existing.setId("1");

        Book updated = new Book("New", "New Author", "New Genre", 5);

        when(bookRepository.findById("1")).thenReturn(Optional.of(existing));
        when(bookRepository.save(any())).thenReturn(existing);

        Optional<Book> result = bookService.updateBook("1", updated);
        assertTrue(result.isPresent());
        assertEquals("New", result.get().getTitle());
    }

    @Test
    public void testDeleteBook_success() {
        when(bookRepository.existsById("1")).thenReturn(true);
        boolean result = bookService.deleteBook("1");

        assertTrue(result);
        verify(bookRepository).deleteById("1");
    }

    @Test
    public void testDeleteBook_notFound() {
        when(bookRepository.existsById("1")).thenReturn(false);
        boolean result = bookService.deleteBook("1");

        assertFalse(result);
        verify(bookRepository, never()).deleteById("1");
    }
}
