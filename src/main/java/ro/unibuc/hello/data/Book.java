package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "books")
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private String genre;
    private String availability;
    private List<Reader> reservers;

    public Book() {}

    public Book(String title, String author, String genre, String availability, List<Reader> reservers) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.availability = availability;
        this.reservers = reservers;
    }

    // Getteri și setteri
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public List<Reader> getReservers() {
        return reservers;
    }

    public void setReservers(List<Reader> reservers) {
        this.reservers = reservers;
    }
}
