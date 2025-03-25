package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.service.AuthService;
import ro.unibuc.hello.data.Librarian;
import ro.unibuc.hello.data.Reader;
import ro.unibuc.hello.data.Librarian;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/reader")
    public String registerReader(@RequestBody Reader reader) {
        return authService.registerReader(reader);
    }

    @PostMapping("/register/librarian")
    public String registerLibrarian(@RequestBody Librarian librarian) {
        return authService.registerLibrarian(librarian);
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password) {
        return authService.login(email, password);
    }
}
