package main.java.ro.unibuc.hello.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "genre")
enum Genre {
    FICTION,
    NON_FICTION,
    MYSTERY,
    ROMANCE,
    HORROR,
    KIDS
}
