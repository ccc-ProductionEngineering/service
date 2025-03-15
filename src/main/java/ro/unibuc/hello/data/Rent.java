package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "rents")
public class Rent {
    @Id
    private String id;
    private String idReader;
    private String idBook;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Rent() {}

    public Rent(String idReader, String idBook, LocalDate borrowDate, LocalDate returnDate) {
        this.idReader = idReader;
        this.idBook = idBook;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    // Getteri și setteri
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdReader() {
        return idReader;
    }

    public void setIdReader(String idReader) {
        this.idReader = idReader;
    }

    public String getIdBook() {
        return idBook;
    }

    public void setIdBook(String idBook) {
        this.idBook = idBook;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
