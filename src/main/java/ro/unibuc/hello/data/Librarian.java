package ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "librarians")
public class Librarian {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;

    public Librarian() {}

    public Librarian(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "librarian"; // Setează automat rolul
    }

    // Getteri și setteri
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
}
