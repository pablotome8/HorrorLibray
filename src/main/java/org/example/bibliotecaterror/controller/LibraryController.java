package org.example.bibliotecaterror.controller;

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
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    @FXML
    private Button buttonExport;

    @FXML
    private Button buttonFavorites;

    @FXML
    private TableView<Book> tableBook;

    @FXML
    private TableColumn<Book,Boolean> tableFavorites;

    @FXML
    private TableColumn<Book,String> tableTitles;

    private BookRepository repository = BookRepository.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instances();
        initGUI();
        actions();
    }
    private void instances() {
        if (repository.getBooks().isEmpty()) {
            ApiController apiController = new ApiController();
            Library library = apiController.apicall("https://stephen-king-api.onrender.com/api/books");
            repository.getBooks().setAll(library.getData());
            applySavedFavorites();
        }
    }
    private void actions() {
        configureBookTableRowDoubleClick();
        configureFavoritesButton();
        configureExportButton();
    }

    private void configureBookTableRowDoubleClick() {
        tableBook.setRowFactory(table -> {
            TableRow<Book> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!isPrimaryDoubleClick(event) || row.isEmpty()) {
                    return;
                }

                Book selectedBook = row.getItem();
                openSelectedBook(selectedBook);
            });

            return row;
        });
    }

    private void configureFavoritesButtonDoubleClick(){
        buttonFavorites.setOnAction(event -> openFavorites());
    }
    private void configureFavoritesButton() {
        buttonFavorites.setOnAction(event -> openFavorites());
    }

    private void configureExportButton() {
        buttonExport.setOnAction(event -> saveFavorites());
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

            Stage stage = createBookViewStage(root);
            stage.show();

            closeCurrentWindow();

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

            Stage stage = new Stage();
            stage.setTitle("Favorite Books");
            stage.setScene(new Scene(root));
            stage.show();

            closeCurrentWindow();

        } catch (IOException e) {
            showError("The favorites view couldn't be opened", e);
        }
    }

    private Stage createBookViewStage(Parent root) {
        Stage stage = new Stage();
        stage.setTitle("Selected Book");
        stage.setScene(new Scene(root));
        return stage;
    }

    private void closeCurrentWindow() {
        Stage currentStage = (Stage) tableBook.getScene().getWindow();
        currentStage.close();
    }

    private void showError(String message, Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
    private void initColumns() {
        tableTitles.setCellValueFactory(new PropertyValueFactory<>("title"));
        tableFavorites.setCellValueFactory(cellData -> cellData.getValue().favoriteProperty());
    }

    private void initFavoritesColumn() {
        tableBook.setEditable(true);
        tableFavorites.setEditable(true);

        tableFavorites.setCellValueFactory(cellData -> cellData.getValue().favoriteProperty());
        tableFavorites.setCellFactory(CheckBoxTableCell.forTableColumn(tableFavorites));
    }

    private void loadTableData() {
        tableBook.setItems(repository.getBooks());
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
            alert.setHeaderText("Favorites saved correctly");
            alert.setContentText("Your favorite books will be loaded next time.");
            alert.showAndWait();

        } catch (Exception e) {
            showError("The favorites couldn't be saved", e);
        }
    }

    private void initGUI() {
            initColumns();
            initFavoritesColumn();
            loadTableData();
        }

    }

