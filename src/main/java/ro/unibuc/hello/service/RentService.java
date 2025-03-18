package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.data.Rent;
import ro.unibuc.hello.data.Reservation;
import ro.unibuc.hello.repository.RentRepository;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.repository.ReaderRepository;
import ro.unibuc.hello.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;


@Service
public class RentService {

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public Rent rentBook(String idReader, String idBook) {
        // Căutăm cartea după ID
        Optional<Book> optionalBook = bookRepository.findById(idBook);
        if (optionalBook.isEmpty()) {
            throw new RuntimeException("Book not found");
        }
    
        // Căutăm cititorul după ID
        Optional<Reader> optionalReader = readerRepository.findById(idReader);
        if (!optionalReader.isPresent()) {
            throw new RuntimeException("Reader not found");
        }
        Reader reader = optionalReader.get();
        Book book = optionalBook.get();
    
        // Verificăm dacă există copii disponibile
        if (book.getCopies() == 0) {
            throw new RuntimeException("Book is not available for rent");
        }
    
        // Obținem lista de cititori din coada de rezervare (readerIds)
        Optional<Reservation> optionalReservation = reservationRepository.findByBookIdAndReaderIdsContaining(idBook, reader.getEmail());
        if (optionalReservation.isEmpty()) {
            throw new RuntimeException("No reservation found for this book");
        }
    
        Reservation reservation = optionalReservation.get();
        List<String> readerIds = reservation.getReaderIds();  // Obținem direct lista
    
        // Verificăm dacă cititorul curent este primul în coadă
        if (!readerIds.get(0).equals(reader.getEmail())) {
            throw new RuntimeException("You are not the first in the queue for this book");
        }
    
        // Eliminăm cititorul din coada de rezervare
        readerIds.remove(reader.getEmail());
    
        // Actualizăm rezervarea în baza de date
        reservation.setReaderIds(readerIds);
        reservationRepository.save(reservation);
    
        // Creăm înregistrarea de închiriere
        Rent rent = new Rent(idReader, idBook, LocalDateTime.now(), null);
    
        // Actualizăm numărul de copii disponibile
        int newCopies = book.getCopies() - 1;
        book.setCopies(newCopies);
    
        // Salvăm cartea actualizată și înregistrarea de închiriere
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
        int newCopies = book.getCopies() + 1;
        book.setCopies(newCopies);
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
