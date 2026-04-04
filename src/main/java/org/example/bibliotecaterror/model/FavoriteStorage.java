package org.example.bibliotecaterror.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FavoriteStorage {

    private static final Path FILE_PATH = Path.of("favorites.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void saveFavorites(List<Book> books) {
        List<Integer> favoriteIds = new ArrayList<>();

        for (Book book : books) {
            if (book.isFavorite()) {
                favoriteIds.add(book.getId());
            }
        }

        FavoriteData data = new FavoriteData(favoriteIds);

        try (Writer writer = Files.newBufferedWriter(FILE_PATH)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error saving favorites", e);
        }
    }

    public static List<Integer> loadFavoriteIds() {
        if (!Files.exists(FILE_PATH)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(FILE_PATH)) {
            FavoriteData data = GSON.fromJson(reader, FavoriteData.class);

            if (data == null || data.getFavoriteIds() == null) {
                return new ArrayList<>();
            }

            return data.getFavoriteIds();

        } catch (IOException e) {
            throw new RuntimeException("Error loading favorites", e);
        }
    }
}