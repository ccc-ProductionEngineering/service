package ro.unibuc.hello.service;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    @Autowired
    BookRepository bookRepository;

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Optional<Book> getByAvailableTitle(String title) {
        List<Book> books = bookRepository.findByTitle(title);

        return books.stream()
                .filter(book -> book.getAvailability() == "AVAILABLE")
                .findFirst();
    }
}