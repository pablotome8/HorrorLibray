package org.example.bibliotecaterror.controller;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.bibliotecaterror.MainAplication;
import org.example.bibliotecaterror.model.Book;
import org.example.bibliotecaterror.model.BookRepository;
import org.example.bibliotecaterror.model.FavoriteStorage;
import org.example.bibliotecaterror.model.Library;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    private static final String API_URL = "https://stephen-king-api.onrender.com/api/books";

    @FXML
    private Button buttonExport;

    @FXML
    private Button buttonFavorites;

    @FXML
    private Button buttonDetails;

    @FXML
    private TextField searchField;

    @FXML
    private Label labelCount;

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
    private TableColumn<Book, Boolean> tableFavorites;

    private final BookRepository repository = BookRepository.getInstance();
    private FilteredList<Book> filteredBooks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instances();
        initGUI();
        actions();
    }

    private void instances() {
        if (repository.getBooks().isEmpty()) {
            try {
                ApiController apiController = new ApiController();
                Library library = apiController.apicall(API_URL);
                if (library != null && library.getData() != null) {
                    repository.getBooks().setAll(library.getData());
                }
                applySavedFavorites();
            } catch (Exception e) {
                showError("Could not load books from the Stephen King API", e);
            }
        }

        for (Book book : repository.getBooks()) {
            book.favoriteProperty().addListener((obs, oldVal, newVal) -> updateCounters());
        }
    }

    private void initGUI() {
        initColumns();
        initFavoritesColumn();
        loadTableDataWithFilter();
        updateCounters();
    }

    private void initColumns() {
        tableTitles.setCellValueFactory(new PropertyValueFactory<>("title"));
        tableYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        tablePublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        tablePages.setCellValueFactory(new PropertyValueFactory<>("pages"));
        tableFavorites.setCellValueFactory(cellData -> cellData.getValue().favoriteProperty());
    }

    private void initFavoritesColumn() {
        tableBook.setEditable(true);
        tableFavorites.setEditable(true);
        tableFavorites.setCellValueFactory(cellData -> cellData.getValue().favoriteProperty());
        tableFavorites.setCellFactory(CheckBoxTableCell.forTableColumn(tableFavorites));
    }

    private void loadTableDataWithFilter() {
        filteredBooks = new FilteredList<>(repository.getBooks(), b -> true);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, query) -> {
                String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
                filteredBooks.setPredicate(book -> matchesSearch(book, normalized));
                updateCounters();
            });
        }

        SortedList<Book> sortedBooks = new SortedList<>(filteredBooks);
        sortedBooks.comparatorProperty().bind(tableBook.comparatorProperty());
        tableBook.setItems(sortedBooks);
        tableBook.setPlaceholder(new Label("No books match your current search filter."));
    }

    private boolean matchesSearch(Book book, String query) {
        if (query.isEmpty()) {
            return true;
        }
        if (book.getTitle() != null && book.getTitle().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        if (book.getPublisher() != null && book.getPublisher().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        if (book.getIsbn() != null && book.getIsbn().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        return book.getYear() != null && String.valueOf(book.getYear()).contains(query);
    }

    private void updateCounters() {
        int visibleCount = filteredBooks != null ? filteredBooks.size() : repository.getBooks().size();
        long favCount = repository.getBooks().stream().filter(Book::isFavorite).count();

        if (labelCount != null) {
            labelCount.setText(visibleCount + (visibleCount == 1 ? " Book" : " Books"));
        }
        if (labelFavCount != null) {
            labelFavCount.setText("★ " + favCount + (favCount == 1 ? " Favorite" : " Favorites"));
        }
    }

    private void actions() {
        configureBookTableRowDoubleClick();
        configureFavoritesButton();
        configureExportButton();
        configureDetailsButton();
    }

    private void configureBookTableRowDoubleClick() {
        tableBook.setRowFactory(table -> {
            TableRow<Book> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!isPrimaryDoubleClick(event) || row.isEmpty()) {
                    return;
                }
                openSelectedBook(row.getItem());
            });

            return row;
        });
    }

    private void configureFavoritesButton() {
        buttonFavorites.setOnAction(event -> openFavorites());
    }

    private void configureExportButton() {
        buttonExport.setOnAction(event -> saveFavorites());
    }

    private void configureDetailsButton() {
        if (buttonDetails != null) {
            buttonDetails.setOnAction(event -> {
                Book selected = tableBook.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("No Book Selected");
                    alert.setHeaderText("Select a book first");
                    alert.setContentText("Please select a book row from the table (or double-click any row) to inspect its details.");
                    alert.showAndWait();
                    return;
                }
                openSelectedBook(selected);
            });
        }
    }

    private boolean isPrimaryDoubleClick(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2;
    }

    private void openSelectedBook(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainAplication.class.getResource("book-view.fxml")
            );

            Parent root = loader.load();
            BookViewController controller = loader.getController();
            controller.setBook(book);
            controller.initGUI();

            Stage stage = getCurrentStage();
            stage.setTitle("Book Dossier — " + (book.getTitle() != null ? book.getTitle() : "Selected Book"));
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();

        } catch (IOException e) {
            showError("The book view couldn't be opened", e);
        }
    }

    private void openFavorites() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainAplication.class.getResource("favorites-view.fxml")
            );

            Parent root = loader.load();
            Stage stage = getCurrentStage();
            stage.setTitle("Favorite Books — Stephen King's Library");
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();

        } catch (IOException e) {
            showError("The favorites view couldn't be opened", e);
        }
    }

    private Stage getCurrentStage() {
        return (Stage) tableBook.getScene().getWindow();
    }

    private void applySavedFavorites() {
        var favoriteIds = FavoriteStorage.loadFavoriteIds();

        for (Book book : repository.getBooks()) {
            book.setFavorite(favoriteIds.contains(book.getId()));
        }
    }

    private void saveFavorites() {
        try {
            FavoriteStorage.saveFavorites(repository.getBooks());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saved");
            alert.setHeaderText("Favorites saved to favorites.json");
            alert.setContentText("Your favorite books collection will be automatically restored next time.");
            alert.showAndWait();

        } catch (Exception e) {
            showError("The favorites couldn't be saved", e);
        }
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
