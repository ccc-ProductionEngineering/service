package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.service.BookService;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostMapping("/add")
    public String addBook(@RequestBody Book book) {
        bookService.addBook(book);
        return "Cartea a fost adăugată cu succes!";
    }

    @GetMapping("/availabletitle")
    public Optional<Book> getByAvailableTitle(@RequestBody String title) {
        return bookService.getByAvailableTitle(title);
    }
}
