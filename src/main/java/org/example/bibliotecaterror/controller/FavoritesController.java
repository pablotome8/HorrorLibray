package org.example.bibliotecaterror.controller;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import org.example.bibliotecaterror.model.FavoriteStorage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FavoritesController implements Initializable {

    private static final String LIBRARY_VIEW = "library-view.fxml";
    private static final String BOOK_VIEW = "book-view.fxml";
    private static final String LIBRARY_TITLE = "Stephen King's Horror Library";

    @FXML
    private Button buttonLibrary;

    @FXML
    private Button buttonSaveFavorites;

    @FXML
    private Button buttonRemoveFavorite;

    @FXML
    private Button buttonDetails;

    @FXML
    private Label labelFavCount;

    @FXML
    private TableView<Book> tableBook;

    @FXML
    private TableColumn<Book, String> tableTitles;

    @FXML
    private TableColumn<Book, Integer> tableYear;

    @FXML
    private TableColumn<Book, String> tablePublisher;

    @FXML
    private TableColumn<Book, Integer> tablePages;

    @FXML
    private TableColumn<Book, String> tableISBN;

    private final BookRepository repository = BookRepository.getInstance();
    private FilteredList<Book> favoriteBooks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initGUI();
        initActions();
    }

    private void initGUI() {
        initColumns();
        loadFavoriteBooks();
        updateCounter();
    }

    private void initColumns() {
        tableTitles.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (tableYear != null) {
            tableYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        }
        if (tablePublisher != null) {
            tablePublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        }
        if (tablePages != null) {
            tablePages.setCellValueFactory(new PropertyValueFactory<>("pages"));
        }
        if (tableISBN != null) {
            tableISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        }
    }

    private void loadFavoriteBooks() {
        favoriteBooks = new FilteredList<>(
                repository.getBooks(),
                Book::isFavorite
        );
        tableBook.setItems(favoriteBooks);
        tableBook.setPlaceholder(new Label("You haven't marked any favorite books yet. Return to the Library to add some!"));
    }

    private void updateCounter() {
        if (labelFavCount != null && favoriteBooks != null) {
            int count = favoriteBooks.size();
            labelFavCount.setText("★ " + count + (count == 1 ? " Saved" : " Saved"));
        }
    }

    private void initActions() {
        buttonLibrary.setOnAction(event -> openLibrary());
        configureBookTableDoubleClick();

        if (buttonSaveFavorites != null) {
            buttonSaveFavorites.setOnAction(event -> saveFavoritesToDisk());
        }
        if (buttonRemoveFavorite != null) {
            buttonRemoveFavorite.setOnAction(event -> removeSelectedFavorite());
        }
        if (buttonDetails != null) {
            buttonDetails.setOnAction(event -> {
                Book selected = tableBook.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openBookView(selected);
                }
            });
        }
    }

    private void removeSelectedFavorite() {
        Book selected = tableBook.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        selected.setFavorite(false);
        favoriteBooks.setPredicate(Book::isFavorite);
        FavoriteStorage.saveFavorites(repository.getBooks());
        updateCounter();
    }

    private void saveFavoritesToDisk() {
        try {
            FavoriteStorage.saveFavorites(repository.getBooks());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saved");
            alert.setHeaderText("Favorites updated");
            alert.setContentText("Your favorite books collection has been saved to favorites.json.");
            alert.showAndWait();
        } catch (Exception e) {
            showError("Could not save favorites", e);
        }
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
            showError("Could not open the library view", e);
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
            showView(stage, root, "Book Dossier — " + (book.getTitle() != null ? book.getTitle() : "Selected Book"));

        } catch (IOException e) {
            showError("Could not open the book dossier view", e);
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
        stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
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
