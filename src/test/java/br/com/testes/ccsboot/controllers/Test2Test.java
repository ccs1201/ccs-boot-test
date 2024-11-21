package br.com.testes.ccsboot.controllers;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Test2Test {

    private HttpClient client;

    @Test
    void test() {

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test/2"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"mensagem\":\"teste\"}"))
                .build();

        assertDoesNotThrow(() -> {
            HttpResponse<String> response = client
                    .sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                    .get();
            assertEquals(201, response.statusCode());

        });
    }
}