package ro.unibuc.hello.data;
import java.util.ArrayList;
import java.util.List;

class Reader extends User {
    private List<Book> borrowedBooks;

    public Reader(String name) {
        super(name);
        this.borrowedBooks = new ArrayList<>();
    }

    // Get the name of the reader
    public String getName() {
        return name;
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

}