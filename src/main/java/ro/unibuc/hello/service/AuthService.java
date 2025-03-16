package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ro.unibuc.hello.config.JwtUtil;
import ro.unibuc.hello.data.Librarian;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.repository.LibrarianRepository;
import ro.unibuc.hello.repository.ReaderRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private LibrarianRepository librarianRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public String registerReader(Reader reader) {
        if (readerRepository.existsByEmail(reader.getEmail()) || librarianRepository.existsByEmail(reader.getEmail())) {
            return "Email already exists!";
        }
        Reader readerSave = new Reader(reader.getName(), reader.getEmail(), passwordEncoder.encode(reader.getPassword()), "USER");
        readerRepository.save(readerSave);
        return "Reader registered successfully!";
    }

    public String registerLibrarian(Librarian librarian) {
        if (readerRepository.existsByEmail(librarian.getEmail()) || librarianRepository.existsByEmail(librarian.getEmail())) {
            return "Email already exists!";
        }
        Librarian librarianSave = new Librarian(librarian.getName(), librarian.getEmail(), passwordEncoder.encode(librarian.getPassword()), "ADMIN");
        librarianRepository.save(librarianSave);
        return "Librarian registered successfully!";
    }

    public String login(String email, String password) {
        Optional<Reader> reader = readerRepository.findByEmail(email);
        Optional<Librarian> librarian = librarianRepository.findByEmail(email);

        if (reader.isPresent() && passwordEncoder.matches(password, reader.get().getPassword())) {
            return jwtUtil.generateToken(email, "USER");
        } else if (librarian.isPresent() && passwordEncoder.matches(password, librarian.get().getPassword())) {
            return jwtUtil.generateToken(email, "ADMIN");
        }
        return "Invalid email or password!";
    }
}
