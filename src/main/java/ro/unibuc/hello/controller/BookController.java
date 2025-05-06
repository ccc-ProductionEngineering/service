package ro.unibuc.hello.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.service.BookService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final MeterRegistry meterRegistry;

    private final Counter addBookCounter;
    private final Counter getAllBooksCounter;
    private final Counter updateBookCounter;
    private final Counter deleteBookCounter;
    private final Timer addBookTimer;

    @Autowired
    public BookController(BookRepository bookRepository, BookService bookService, MeterRegistry meterRegistry) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.meterRegistry = meterRegistry;

        // Inițializăm metricile
        this.addBookCounter = meterRegistry.counter("book_add_requests_total");
        this.getAllBooksCounter = meterRegistry.counter("book_get_all_requests_total");
        this.updateBookCounter = meterRegistry.counter("book_update_requests_total");
        this.deleteBookCounter = meterRegistry.counter("book_delete_requests_total");
        this.addBookTimer = meterRegistry.timer("book_add_request_duration_seconds");
    }

    @PostMapping("/add")
    public String addBook(@RequestBody Book book) {
        addBookCounter.increment(); // incrementăm numărul de cereri de adăugare

        return addBookTimer.record(() -> { // măsurăm timpul de execuție al adăugării unei cărți
            bookService.addBook(book);
            return "Cartea a fost adaugata cu succes!";
        });
    }

    @GetMapping("/all")
    public List<Book> getAllBooks() {
        getAllBooksCounter.increment(); // incrementăm numărul de cereri pentru toate cărțile
        return bookService.getAllBooks();
    }

    @PutMapping("/update/{id}")
    public String updateBook(@PathVariable("id") String id, @RequestBody Book bookDetails) {
        updateBookCounter.increment(); // incrementăm numărul de update-uri
        return bookService.updateBook(id, bookDetails)
                .map(book -> "Cartea a fost actualizata cu succes!")
                .orElse("Cartea nu a fost gasita!");
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") String id) {
        deleteBookCounter.increment(); // incrementăm numărul de ștergeri
        return bookService.deleteBook(id) ? "Cartea a fost stearsa cu succes!" : "Cartea nu a fost gasita!";
    }
}
