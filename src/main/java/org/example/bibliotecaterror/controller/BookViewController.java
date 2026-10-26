package org.example.bibliotecaterror.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.example.bibliotecaterror.MainAplication;
import org.example.bibliotecaterror.model.Book;
import org.example.bibliotecaterror.model.BookRepository;
import org.example.bibliotecaterror.model.FavoriteStorage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public class BookViewController implements Initializable {

    private static final String LIBRARY_VIEW = "library-view.fxml";
    private static final String FAVORITES_VIEW = "favorites-view.fxml";
    private static final String LIBRARY_TITLE = "Stephen King's Horror Library";
    private static final String FAVORITES_TITLE = "Favorite Books — Stephen King's Library";

    @FXML
    private Button buttonLibrary;

    @FXML
    private Button buttonFavorites;

    @FXML
    private Button buttonToggleFavorite;

    @FXML
    private Label labelCoverTitle;

    @FXML
    private Label badgeYear;

    @FXML
    private Label badgePages;

    @FXML
    private Text textTitle;

    @FXML
    private Text textYear;

    @FXML
    private Text textISBN;

    @FXML
    private Text textPublisher;

    @FXML
    private Text textPages;

    @FXML
    private TextArea textNotes;

    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

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

        if (buttonToggleFavorite != null) {
            buttonToggleFavorite.setOnAction(event -> toggleFavoriteStatus());
        }
    }

    private void renderBook() {
        String title = safeText(book.getTitle(), "Untitled Work");
        String year = book.getYear() != null ? String.valueOf(book.getYear()) : "Unknown";
        String isbn = safeText(book.getIsbn(), "N/A");
        String publisher = safeText(book.getPublisher(), "Unknown Publisher");
        String pages = book.getPages() != null ? book.getPages() + " pages" : "N/A";

        textTitle.setText(title);
        textYear.setText(year);
        textISBN.setText(isbn);
        if (textPublisher != null) {
            textPublisher.setText(publisher);
        }
        if (textPages != null) {
            textPages.setText(pages);
        }
        if (labelCoverTitle != null) {
            labelCoverTitle.setText(title);
        }
        if (badgeYear != null) {
            badgeYear.setText(year);
        }
        if (badgePages != null) {
            badgePages.setText(pages);
        }
        if (textNotes != null) {
            textNotes.setText(formatNotes(book.getNotes()));
        }

        updateFavoriteButtonState();
    }

    private String formatNotes(List<String> notes) {
        if (notes == null || notes.isEmpty()) {
            return "No additional archival notes recorded for this edition.";
        }
        StringBuilder sb = new StringBuilder();
        for (String note : notes) {
            if (note != null && !note.isBlank()) {
                sb.append("• ").append(note.trim()).append("\n\n");
            }
        }
        return sb.length() > 0 ? sb.toString().trim() : "No additional archival notes recorded for this edition.";
    }

    private void toggleFavoriteStatus() {
        if (book == null) {
            return;
        }
        book.setFavorite(!book.isFavorite());
        FavoriteStorage.saveFavorites(BookRepository.getInstance().getBooks());
        updateFavoriteButtonState();
    }

    private void updateFavoriteButtonState() {
        if (buttonToggleFavorite == null || book == null) {
            return;
        }
        if (book.isFavorite()) {
            buttonToggleFavorite.setText("★ In Your Favorites (Click to Remove)");
        } else {
            buttonToggleFavorite.setText("☆ Add to Favorites");
        }
    }

    private String safeText(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
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
            showError("Could not open view: " + title, e);
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
