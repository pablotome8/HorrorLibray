package org.example.bibliotecaterror.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.example.bibliotecaterror.MainAplication;
import org.example.bibliotecaterror.model.Book;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class BookViewController implements Initializable {

    private static final String LIBRARY_VIEW = "library-view.fxml";
    private static final String FAVORITES_VIEW = "favorites-view.fxml";
    private static final String LIBRARY_TITLE = "Biblioteca Stephen King";
    private static final String FAVORITES_TITLE = "Favorite Books";

    @FXML
    private Button buttonLibrary;

    @FXML
    private Button buttonFavorites;

    @FXML
    private Text textISBN;

    @FXML
    private Text textTitle;

    @FXML
    private Text textYear;

    private Book book;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initActions();
    }

    public void initGUI() {
        if (book == null) {
            return;
        }

        renderBook();
    }

    private void initActions() {
        buttonLibrary.setOnAction(event -> openLibrary());
        buttonFavorites.setOnAction(event -> openFavorites());
    }

    private void renderBook() {
        textTitle.setText(safeText(book.getTitle()));
        textISBN.setText(safeText(book.getIsbn()));
        textYear.setText(book.getYear() != null ? String.valueOf(book.getYear()) : "");
    }

    private String safeText(String value) {
        return value != null ? value : "";
    }

    private void openLibrary() {
        navigateTo(LIBRARY_VIEW, LIBRARY_TITLE, buttonLibrary);
    }

    private void openFavorites() {
        navigateTo(FAVORITES_VIEW, FAVORITES_TITLE, buttonFavorites);
    }

    private void navigateTo(String fxmlPath, String title, Button sourceButton) {
        try {
            Parent root = loadView(fxmlPath);
            Stage stage = getCurrentStage(sourceButton);
            showView(stage, root, title);
        } catch (IOException e) {
            showError("No se pudo abrir la vista: " + title, e);
        }
    }

    private Parent loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainAplication.class.getResource(fxmlPath)
        );
        return loader.load();
    }

    private Stage getCurrentStage(Button sourceButton) {
        return (Stage) sourceButton.getScene().getWindow();
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