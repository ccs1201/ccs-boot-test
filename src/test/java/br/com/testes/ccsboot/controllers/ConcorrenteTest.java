package br.com.testes.ccsboot.controllers;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcorrenteTest {
    private final int NUM_REUESTS = 100;
    private HttpClient client;

    @Test
    void test() {

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        CompletableFuture[] futures = new CompletableFuture[NUM_REUESTS];

        for (int i = 0; i < NUM_REUESTS; i++) {
            final var body = i;
            futures[i] = CompletableFuture.runAsync(() -> doRequest("" + body),
                    ForkJoinPool.commonPool());
        }

        CompletableFuture.allOf(futures).join();
    }

    private void doRequest(String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/concorrente"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(java.time.Duration.ofSeconds(5))
                .build();

        assertDoesNotThrow(() -> {
            HttpResponse<String> response = client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            assertEquals(200, response.statusCode());
            assertEquals(body, response.body());
            System.out.println("request body: " + body + " | response body: " + response.body());
        });
    }
}