package ro.unibuc.hello.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ro.unibuc.hello.data.Rent;
import java.util.List;

public interface RentRepository extends MongoRepository<Rent, String> {
    List<Rent> findByIdReader(String idReader);
    List<Rent> findByIdBook(String idBook);
}
