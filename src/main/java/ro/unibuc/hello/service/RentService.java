package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.data.Rent;
import ro.unibuc.hello.repository.RentRepository;
import ro.unibuc.hello.repository.BookRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RentService {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private BookRepository bookRepository;

    public Rent rentBook(String idReader, String idBook) {
        Optional<Book> optionalBook = bookRepository.findById(idBook);
        if (optionalBook.isEmpty()) {
            throw new RuntimeException("Book not found");
        }
        Book book = optionalBook.get();
        if (!"AVAILABLE".equalsIgnoreCase(book.getAvailability())) {
            throw new RuntimeException("Book is not available for rent");
        }
        Rent rent = new Rent(idReader, idBook, LocalDateTime.now(), null);
        book.setAvailability("RENTED");
        bookRepository.save(book);
        return rentRepository.save(rent);
    }


    public Rent returnBook(String id) {
        Optional<Rent> rentOpt = rentRepository.findById(id);
        if (rentOpt.isEmpty()) {
            throw new IllegalArgumentException("Rent not found with id " + id);
        }
        Rent rent = rentOpt.get();
        if (rent.getReturnDate() != null) {
            throw new IllegalStateException("Book has already been returned.");
        }
        rent.setReturnDate(LocalDateTime.now());
        Optional<Book> bookOpt = bookRepository.findById(rent.getIdBook());
        if (bookOpt.isEmpty()) {
            throw new IllegalArgumentException("Book not found with id " + rent.getIdBook());
        }
        Book book = bookOpt.get();
        book.setAvailability("AVAILABLE");
        bookRepository.save(book);
        return rentRepository.save(rent);
    }
    

    public List<Rent> getRentsByReaderId(String readerId) {
        return rentRepository.findByIdReader(readerId);
    }

    public List<Rent> getRentsByBookId(String bookId) {
        return rentRepository.findByIdBook(bookId);
    }

    public Optional<Rent> getRentsByBookIdAndReaderId(String readerId, String bookId) {
        List<Rent> rents = rentRepository.findByIdReaderAndIdBook(readerId, bookId);

        return rents.stream()
                .filter(rent -> rent.getReturnDate() == null)
                .findFirst();
    }
}
