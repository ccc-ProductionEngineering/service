package ro.unibuc.hello.service;
import ro.unibuc.hello.data.Librarian;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.repository.ReaderRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReaderService {
    @Autowired
    private ReaderRepository readerRepository;

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
}