package ro.unibuc.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ro.unibuc.hello.data.InformationEntity;
import ro.unibuc.hello.data.InformationRepository;
import ro.unibuc.hello.repository.BookRepository;
import ro.unibuc.hello.repository.ReaderRepository;
import ro.unibuc.hello.repository.LibrarianRepository;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = { 
        InformationRepository.class, 
        BookRepository.class, 
        ReaderRepository.class, 
        LibrarianRepository.class 
})
public class HelloApplication {

    @Autowired
    private InformationRepository informationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private LibrarianRepository librarianRepository;

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

    @PostConstruct
    public void runAfterObjectCreated() {
        informationRepository.deleteAll();
        informationRepository.save(new InformationEntity("Overview",
                "This is an example of using a data storage engine running separately from our applications server"));
    }
}
