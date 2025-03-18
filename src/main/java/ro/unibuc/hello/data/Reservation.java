package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document(collection = "reservations")
public class Reservation {
    @Id
    private String id;
    private String bookId;
    private List<String> readerIds = new LinkedList<>();

    public Reservation() {}

    public Reservation(String bookId) {
        this.bookId = bookId;
    }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public List<String> getReaderIds() {
        return readerIds;
    }

    public void setReaderIds(List<String> readerIds) {
        this.readerIds = readerIds;
    }

    public void addReader(String readerId) {
        readerIds.add(readerId);
    }

    public void removeReader(String readerId) {
        readerIds.remove(readerId);
    }

    public int getQueuePosition(String readerId) {
        return readerIds.indexOf(readerId) + 1;
    }
}
