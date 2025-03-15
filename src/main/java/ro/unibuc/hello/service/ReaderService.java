package ro.unibuc.hello.service;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.repository.ReaderRepository;
import ro.unibuc.hello.repository.RentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReaderService {
    @Autowired
    private ReaderRepository readerRepository;
    @Autowired
    private RentRepository rentRepository;


    public List<Reader> getAllReaders() {
        return readerRepository.findAll();
    }

    public Reader getReaderByEmail(String email) {
        Reader reader = readerRepository.findByEmail(email).orElse(null);
        if (reader == null){
            return null;
        }
        return reader;
    }

    // get all reader with borrowed books
    public List<Reader> getReadersWithBorrowedBooks() {
        return readerRepository.findAll().stream()
                .filter(reader -> !rentRepository.findByIdReader(reader.getId()).isEmpty())
                .collect(Collectors.toList());
    }

    // get all reader with late books
    public List<Reader> getLateReaders() {
        LocalDateTime now = LocalDateTime.now();

        return readerRepository.findAll().stream()
                .filter(reader -> rentRepository.findByIdReader(reader.getId()).stream()
                        .anyMatch(rent -> rent.getReturnDate().isBefore(now)))
                .collect(Collectors.toList());
    }

    // ban all late readers
    public List<Reader> banLateReaders() {
        List<Reader> lateReaders = getLateReaders();

        lateReaders.forEach(reader -> reader.setRole("BANNED"));
        readerRepository.saveAll(lateReaders);

        return lateReaders;
    }

    //get all banned readers
    public List<Reader> getBannedReaders() {
        return readerRepository.findAll().stream()
                .filter(reader -> reader.getRole().equals("BANNED"))
                .collect(Collectors.toList());
    }

    //unban a reader
    public Boolean unbanReader(String id) {
        Reader reader = readerRepository.findById(id).orElse(null);
        if (reader == null){
            return null;
        }
        reader.setRole("USER");
        readerRepository.save(reader);
        return true;
    }

   

    
}