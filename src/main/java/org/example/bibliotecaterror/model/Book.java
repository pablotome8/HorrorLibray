package org.example.bibliotecaterror.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Book {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("Year")
    @Expose
    private Integer year;

    @SerializedName("Title")
    @Expose
    private String title;

    @SerializedName("handle")
    @Expose
    private String handle;

    @SerializedName("Publisher")
    @Expose
    private String publisher;

    @SerializedName("ISBN")
    @Expose
    private String isbn;

    @SerializedName("Pages")
    @Expose
    private Integer pages;

    @SerializedName("Notes")
    @Expose
    private List<String> notes;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    private final BooleanProperty favorite = new SimpleBooleanProperty(false);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFavorite() {
        return favorite.get();
    }

    public void setFavorite(boolean favorite) {
        this.favorite.set(favorite);
    }

    public BooleanProperty favoriteProperty() {
        return favorite;
    }
}
