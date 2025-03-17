package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.data.Reservation;
import ro.unibuc.hello.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

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


}
