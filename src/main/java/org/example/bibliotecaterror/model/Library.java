package org.example.bibliotecaterror.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class Library {

    @SerializedName("data")
    @Expose
    private List<Book> data;

}


