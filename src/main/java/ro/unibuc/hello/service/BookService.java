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

    // get all books
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }
    
    // add a book
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    // get a book by title
    public Optional<Book> getByAvailableTitle(String title) {
        List<Book> books = bookRepository.findByTitle(title);

        return books.stream()
                .filter(book -> book.getAvailability() == "AVAILABLE")
                .findFirst();
    }

    // update a book
    public Optional<Book> updateBook(String id, Book bookDetails)
    {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setGenre(bookDetails.getGenre());
            book.setAvailability(bookDetails.getAvailability());
            return bookRepository.save(book);
        });
    }

    // delete a book
    public boolean deleteBook(String id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}