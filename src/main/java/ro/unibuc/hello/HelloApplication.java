package ro.unibuc.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import ro.unibuc.hello.data.InformationEntity;
import ro.unibuc.hello.data.InformationRepository;
import ro.unibuc.hello.data.Book;
import ro.unibuc.hello.data.User;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = InformationRepository.class)
public class HelloApplication {

	@Autowired
	private InformationRepository informationRepository;

	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

	@PostConstruct
	public void runAfterObjectCreated() {
		informationRepository.deleteAll();
		informationRepository.save(new Book("Titlu 1","Autor 1", "Crima"));
		informationRepository.save(new User("Nume"));
	}

}
