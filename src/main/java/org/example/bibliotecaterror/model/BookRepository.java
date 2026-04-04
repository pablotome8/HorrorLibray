package org.example.bibliotecaterror.model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;


public class BookRepository {

    private static final BookRepository INSTANCE = new BookRepository();

    private final ObservableList<Book> books = FXCollections.observableArrayList();

    private BookRepository () {

    }

    public static BookRepository getInstance() {
        return INSTANCE;
    }

    public ObservableList<Book> getBooks() {
        return books;
    }
}
