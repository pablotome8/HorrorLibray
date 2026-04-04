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
    private  Integer id;
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
