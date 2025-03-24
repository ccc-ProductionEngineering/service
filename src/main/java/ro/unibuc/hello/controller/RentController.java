package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.Rent;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.service.RentService;
import ro.unibuc.hello.service.ReaderService;
import ro.unibuc.hello.service.BookService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;


@RestController
public class RentController {

    @Autowired
    private RentService rentService;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookService bookService;

    @PostMapping("/rent")
    public ResponseEntity<Rent> rentBook(@RequestParam String bookId, Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        Reader reader = readerService.getReaderByEmail(email);
        String readerId = reader.getId();

        Rent rent = rentService.rentBook(readerId, bookId);
        return new ResponseEntity<>(rent, HttpStatus.CREATED);
    }

    //not working
    @PostMapping("/rentbytitle")
    public ResponseEntity<Rent> rentBookByTitle(@RequestBody String title) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        Reader reader = readerService.getReaderByEmail(email);
        String readerId = reader.getId();

        String bookId = bookService.getByAvailableTitle(title).get().getId();

        Rent rent = rentService.rentBook(readerId, bookId);
        return new ResponseEntity<>(rent, HttpStatus.CREATED);
    }


    @PostMapping("/return")
    public ResponseEntity<Rent> returnBook(@RequestParam String bookId, Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        Reader reader = readerService.getReaderByEmail(email);
        String readerId = reader.getId();

        String rentId = rentService.getRentsByBookIdAndReaderId(readerId, bookId).get().getId();

        Rent rent = rentService.returnBook(rentId);
        return new ResponseEntity<>(rent, HttpStatus.OK);
    }
}
