package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.service.BookService;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Optional;
import java.util.List;


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

    @GetMapping("/all")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PutMapping("/update/{id}")
    public String updateBook(@PathVariable String id, @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails)
                .map(book -> "Cartea a fost actualizata cu succes!")
                .orElse("Cartea nu a fost gasita!");
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable String id)
    {
        return bookService.deleteBook(id) ? "Cartea a fost stearsa cu succes!" : "Cartea nu a fost gasita!";
    }


}
