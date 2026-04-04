package org.example.bibliotecaterror.controller;

import com.google.gson.Gson;
import org.example.bibliotecaterror.model.Library;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiController {

    public Library apicall(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response;

            try {
                response = client.send(request,HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                Gson gson = new Gson();
                return gson.fromJson(response.body(), Library.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

    }


}
