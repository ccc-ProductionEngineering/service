// BookControllerTest.java
package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.service.BookService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {

    private BookService bookService;
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        bookService = mock(BookService.class);
        bookController = new BookController(mock(BookRepository.class), bookService);
    }

    @Test
    public void testAddBook() {
        Book book = new Book("Title", "Author", "Genre", 2);
        when(bookService.addBook(book)).thenReturn(book);

        String result = bookController.addBook(book);
        assertEquals("Cartea a fost adăugată cu succes!", result);
    }

    @Test
    public void testGetAllBooks() {
        List<Book> books = List.of(new Book(), new Book());
        when(bookService.getAllBooks()).thenReturn(books);

        List<Book> result = bookController.getAllBooks();
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdateBook_success() {
        Book bookDetails = new Book("Updated", "Author", "Genre", 3);
        when(bookService.updateBook("1", bookDetails)).thenReturn(Optional.of(bookDetails));

        String result = bookController.updateBook("1", bookDetails);
        assertEquals("Cartea a fost actualizata cu succes!", result);
    }

    @Test
    public void testUpdateBook_notFound() {
        Book bookDetails = new Book("Updated", "Author", "Genre", 3);
        when(bookService.updateBook("1", bookDetails)).thenReturn(Optional.empty());

        String result = bookController.updateBook("1", bookDetails);
        assertEquals("Cartea nu a fost gasita!", result);
    }

    @Test
    public void testDeleteBook_success() {
        when(bookService.deleteBook("1")).thenReturn(true);

        String result = bookController.deleteBook("1");
        assertEquals("Cartea a fost stearsa cu succes!", result);
    }

    @Test
    public void testDeleteBook_notFound() {
        when(bookService.deleteBook("1")).thenReturn(false);

        String result = bookController.deleteBook("1");
        assertEquals("Cartea nu a fost gasita!", result);
    }
}
