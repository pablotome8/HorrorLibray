package org.example.bibliotecaterror.controller;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.bibliotecaterror.MainAplication;
import org.example.bibliotecaterror.model.Book;
import org.example.bibliotecaterror.model.BookRepository;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FavoritesController implements Initializable {

    private static final String LIBRARY_VIEW = "library-view.fxml";
    private static final String BOOK_VIEW = "book-view.fxml";
    private static final String LIBRARY_TITLE = "Biblioteca Stephen King";
    private static final String BOOK_TITLE = "Selected Book";

    @FXML
    private Button buttonLibrary;

    @FXML
    private TableView<Book> tableBook;

    @FXML
    private TableColumn<Book, String> tableTitles;

    private final BookRepository repository = BookRepository.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initGUI();
        initActions();
    }

    private void initGUI() {
        initColumns();
        loadFavoriteBooks();
    }

    private void initColumns() {
        tableTitles.setCellValueFactory(new PropertyValueFactory<>("title"));
    }

    private void loadFavoriteBooks() {
        FilteredList<Book> favoriteBooks = new FilteredList<>(
                repository.getBooks(),
                Book::isFavorite
        );
        tableBook.setItems(favoriteBooks);
    }

    private void initActions() {
        buttonLibrary.setOnAction(event -> openLibrary());
        configureBookTableDoubleClick();
    }

    private void configureBookTableDoubleClick() {
        tableBook.setRowFactory(table -> {
            TableRow<Book> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!isPrimaryDoubleClick(event) || row.isEmpty()) {
                    return;
                }

                Book selectedBook = row.getItem();
                openBookView(selectedBook);
            });

            return row;
        });
    }

    private boolean isPrimaryDoubleClick(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY
                && event.getClickCount() == 2;
    }

    private void openLibrary() {
        try {
            Parent root = loadView(LIBRARY_VIEW);
            Stage stage = getCurrentStage();
            showView(stage, root, LIBRARY_TITLE);
        } catch (IOException e) {
            showError("No se pudo abrir la biblioteca", e);
        }
    }

    private void openBookView(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainAplication.class.getResource(BOOK_VIEW)
            );

            Parent root = loader.load();
            BookViewController controller = loader.getController();
            controller.setBook(book);
            controller.initGUI();

            Stage stage = getCurrentStage();
            showView(stage, root, BOOK_TITLE);

        } catch (IOException e) {
            showError("No se pudo abrir la vista del libro", e);
        }
    }

    private Parent loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainAplication.class.getResource(fxmlPath)
        );
        return loader.load();
    }

    private Stage getCurrentStage() {
        return (Stage) buttonLibrary.getScene().getWindow();
    }

    private void showView(Stage stage, Parent root, String title) {
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message, Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}