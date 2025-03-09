package main.java.ro.unibuc.hello.data;

import java.io.Reader;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private Genre genre;
    private Availability availability;
    private Reader borrower;
    private Reader reserver;
    private LocalDate reservationDate;
    private LocalDate borrowDate;

    public Book(String title, String author, Genre genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.availability = Availability.AVAILABLE;
        this.reserver = null;
    }


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Genre getGenre() {
        return genre;
    }

    public Reader getReserver() {
        return reserver;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setBorrower(Reader borrower) {
        this.borrower = borrower;
    }

    public void setReserver(Reader reserver) {
        this.reserver = reserver;
    }

    public Reader getBorrower() {
        return borrower;
    }

    // Get the reservation date of the book
    public LocalDate getReservationDate() {
        return reservationDate;
    }

    // Set the reservation date of the book
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }
}