package org.example.bibliotecaterror.model;

import java.util.List;

public class FavoriteData {
    private List<Integer> favoriteIds;

    public FavoriteData() {
    }

    public FavoriteData(List<Integer> favoriteIds) {
        this.favoriteIds = favoriteIds;
    }

    public List<Integer> getFavoriteIds() {
        return favoriteIds;
    }

    public void setFavoriteIds(List<Integer> favoriteIds) {
        this.favoriteIds = favoriteIds;
    }
}

