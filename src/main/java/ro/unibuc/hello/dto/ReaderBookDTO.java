package ro.unibuc.hello.dto;

public class ReaderBookDTO{
    private String idReader;
    private String idBook;

    public ReaderBookDTO() {}

    public ReaderBookDTO(String idReader, String idBook) {
        this.idReader = idReader;
        this.idBook = idBook;
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
}

