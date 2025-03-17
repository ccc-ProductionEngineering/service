package ro.unibuc.hello.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.unibuc.hello.data.Reservation;

import java.util.Optional;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    Optional<Reservation> findByBookId(String bookId);
    Optional<Reservation> findByBookIdAndReaderIdsContaining(String bookId, String readerId);
}
