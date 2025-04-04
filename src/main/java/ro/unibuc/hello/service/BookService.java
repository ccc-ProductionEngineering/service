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
                .filter(book -> book.getCopies() > 0)
                .findFirst();
    }

    // update a book
    public Optional<Book> updateBook(String id, Book bookDetails)
    {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setGenre(bookDetails.getGenre());
            book.setCopies(bookDetails.getCopies());
            return bookRepository.save(book);
        });
    }

    // delete a book
    public boolean deleteBook(String id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            bookRepository.deleteById(id);
            return true;
        } else {
            System.out.println("Carte cu id inexistent: " + id);
            return false;
        }
    }
    
    }
