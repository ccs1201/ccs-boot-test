package br.com.testes.ccsboot.controllers;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcorrenteTest {
    private final int NUM_REUESTS = 10_000;
    private HttpClient client;
    private static final int MAX_SLEEP_TIME = 5;

    private final AtomicInteger maxRequestTime = new AtomicInteger(Integer.MIN_VALUE);
    private final AtomicInteger minRequestTime = new AtomicInteger(Integer.MAX_VALUE);
    private final AtomicInteger totalRequestTime = new AtomicInteger(0);


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
        System.out.println("Max request time: " + maxRequestTime.get() + "ms");
        System.out.println("Min request time: " + minRequestTime.get() + "ms");
        System.out.println("Average request time: " + (totalRequestTime.get() / NUM_REUESTS) + "ms");
    }

    private void doRequest(String body) {

        var sleep = new Random().nextInt(MAX_SLEEP_TIME);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            System.out.println("Erro na requisição n. " + body);
            throw new RuntimeException(e);
        }

        var start = System.currentTimeMillis();

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
            var end = System.currentTimeMillis() - start;
            System.out.println("request body: " + body + " | response body: " + response.body() + " Request time " + end + "ms");
            maxRequestTime.accumulateAndGet((int) end, Math::max);
            minRequestTime.accumulateAndGet((int) end, Math::min);
            totalRequestTime.addAndGet((int) end);
        });
    }
}