package org.example.bibliotecaterror.controller;

import com.google.gson.Gson;
import org.example.bibliotecaterror.model.Library;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiController {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Gson GSON = new Gson();

    public Library apicall(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Unexpected API status code: " + response.statusCode());
            }
            return GSON.fromJson(response.body(), Library.class);
        } catch (IOException e) {
            throw new RuntimeException("Network error while fetching Stephen King catalog", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("API request interrupted", e);
        }
    }
}
