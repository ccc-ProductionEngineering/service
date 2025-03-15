package ro.unibuc.hello.service;
import ro.unibuc.hello.data.Librarian;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.repository.LibrarianRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibrarianService {
    @Autowired
    LibrarianRepository librarianRepository;

    List<Librarian> getAllLibrarians(){
        return librarianRepository.findAll();
    }

    public Librarian addLibrarian(Librarian librarian) {
        return librarianRepository.save(librarian);
    }

    public Librarian getLibrarianByEmail(String email) {
        Librarian librarian = librarianRepository.findByEmail(email).orElse(null);
        if (librarian == null){
            return null;
        }
        return librarian;
    }
}