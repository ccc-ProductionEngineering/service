package ro.unibuc.hello.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.service.ReaderService;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/readers")
public class ReaderController {
    @Autowired
    private ReaderService readerService;

    // 1. Obtine toti utilizatorii cu carti imprumutate
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/borrowers")
    public List<Reader> getReadersWithBorrowedBooks() {
        return readerService.getReadersWithBorrowedBooks();
    }

    // 2. Obtine utilizatorii care au intarziat returnarea
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/late")
    public List<Reader> getLateReaders() {
        return readerService.getLateReaders();
    }

    // 3. Baneaza utilizatorii care au intarziat returnarea
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/banlate")
    public List<Reader> banLateReaders() {
        return readerService.banLateReaders();
    }

    // 4. Obtine toti utilizatorii banati
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/banned")
    public List<Reader> getBannedReaders() {
        return readerService.getBannedReaders();
    }

    // 5. Unban un utilizator
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unban/{id}")
    public String unbanReader(@PathVariable String id) {
        return readerService.unbanReader(id) ? "Utilizatorul a fost debanat cu succes!" : "Utilizatorul nu a fost gasit!";
    }

    // 6. Obtine toti utilizatorii
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<Reader> getAllReaders() {
        return readerService.getAllReaders();
    }

}
