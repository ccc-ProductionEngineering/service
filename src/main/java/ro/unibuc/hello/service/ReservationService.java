package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.data.Reservation;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.repository.ReservationRepository;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.repository.ReaderRepository;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookRepository bookRepository;

    public int reserveBook(String bookId, String readerId) {
        Optional<Reservation> existingReservation = reservationRepository.findByBookId(bookId);

        Reservation reservation;
        if (existingReservation.isPresent()) {
            reservation = existingReservation.get();
        } else {
            reservation = new Reservation(bookId);
        }

        if (!reservation.getReaderIds().contains(readerId)) {
            reservation.addReader(readerId);
            reservationRepository.save(reservation);
        }

        return reservation.getQueuePosition(readerId);
    }

    public boolean unreserveBook(String bookId, String readerId) {
        Optional<Reservation> existingReservation = reservationRepository.findByBookId(bookId);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            reservation.removeReader(readerId);
            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }

    public Optional<Reservation> getReservationByBookId(String bookId) {
        return reservationRepository.findByBookId(bookId);
    }


    public List<Reservation> getReservationsByReaderId(String readerId) {
        return reservationRepository.findAll()
                .stream()
                .filter(reservation -> reservation.getReaderIds().contains(readerId))
                .collect(Collectors.toList());
    }

   public List<Map<String, Object>> getBookTitlesAndQueuePositionsByReaderId(String readerId) {
        return reservationRepository.findAll()
                .stream()
                .filter(reservation -> reservation.getReaderIds().contains(readerId))
                .map(reservation -> {
                    Optional<Book> optionalBook = bookRepository.findById(reservation.getBookId());
                    String bookTitle = optionalBook.map(Book::getTitle).orElse("Unknown Title");
                    int queuePosition = reservation.getQueuePosition(readerId);
                    
                    Map<String, Object> bookInfo = new HashMap<>();
                    bookInfo.put("title", bookTitle);
                    bookInfo.put("queuePosition", queuePosition);
                    
                    return bookInfo;
                })
                .collect(Collectors.toList());
    }


}
