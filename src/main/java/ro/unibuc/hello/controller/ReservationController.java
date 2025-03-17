package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.Reservation;
import ro.unibuc.hello.service.ReservationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reserve")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // Reserve a book
    @PostMapping("/book")
    public ResponseEntity<String> reserveBook(@RequestParam String bookId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated.");
        }
    
        String readerId = authentication.getName();
        int position = reservationService.reserveBook(bookId, readerId);
        return ResponseEntity.ok("You are number " + position + " in the queue.");
    }


    // Unreserve a book
    @PostMapping("/unreserve")
    public ResponseEntity<String> unreserveBook(@RequestParam String bookId, Authentication authentication) {
        String readerId = authentication.getName();
        boolean success = reservationService.unreserveBook(bookId, readerId);
        if (success) {
            return ResponseEntity.ok("Reservation cancelled.");
        } else {
            return ResponseEntity.badRequest().body("No reservation found.");
        }
    }

    @GetMapping("/list/{bookId}")
    public ResponseEntity<Object> getReservationList(@PathVariable String bookId) {
        Optional<Reservation> reservation = reservationService.getReservationByBookId(bookId);
    
        return reservation
            .map(res -> ResponseEntity.ok().body((Object) res)) // Explicitare tipului ca Object
            .orElseGet(() -> ResponseEntity.badRequest().body("No reservations found."));
    }

    @GetMapping("/my-reservations")
public ResponseEntity<?> getUserReservations(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated.");
    }

    String readerId = authentication.getName();
    List<Reservation> userReservations = reservationService.getReservationsByReaderId(readerId);

    if (userReservations.isEmpty()) {
        return ResponseEntity.ok("No reservations found.");
    }

    return ResponseEntity.ok(userReservations);
}

}
