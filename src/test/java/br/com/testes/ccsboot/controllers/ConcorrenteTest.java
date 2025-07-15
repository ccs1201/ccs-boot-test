package br.com.testes.ccsboot.controllers;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConcorrenteTest {
    private final int NUM_REQUESTS = 10_000;
    private HttpClient client;
    private static final int MAX_SLEEP_TIME = 10;
    private static final int EXECUTION_TIME_SECONDS = 60;
    private static final int VIRTUAL_USERS = 50;

    private final AtomicInteger maxRequestTime = new AtomicInteger(Integer.MIN_VALUE);
    private final AtomicInteger minRequestTime = new AtomicInteger(Integer.MAX_VALUE);
    private final AtomicInteger totalRequestTime = new AtomicInteger(0);
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            VIRTUAL_USERS,
            VIRTUAL_USERS,
            60L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>()
    );


    @Test
    void test() {

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        var futures = new ArrayList<CompletableFuture>();

        var start = System.currentTimeMillis();
        var endTime = start + (EXECUTION_TIME_SECONDS * 1000);
        var body = 0;
        var qtdRequestPeriodo = 0;

        while (System.currentTimeMillis() < endTime) {
            final var bodyFinal = body;
            futures.add(
                    CompletableFuture.runAsync(() -> doRequest(bodyFinal),
                            executor));

            body++;
            qtdRequestPeriodo++;
        }

        var futuresArray = futures.toArray(new CompletableFuture[0]);

        executor.shutdown();

        CompletableFuture.allOf(futuresArray).join();

        var end = System.currentTimeMillis() - start;
        System.out.println("Max request time: " + maxRequestTime.get() + "ms");
        System.out.println("Min request time: " + minRequestTime.get() + "ms");
        System.out.println("Average request time: " + (totalRequestTime.get() / qtdRequestPeriodo) + "ms");
        System.out.println("Requests per second: " + String.format("%.2f", (qtdRequestPeriodo * 1000.0 / end)));
    }

    private void doRequest(Integer body) {
        var sleep = new Random().nextInt(MAX_SLEEP_TIME);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/concorrente"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(body)))
                .timeout(java.time.Duration.ofSeconds(5))
                .build();

        assertDoesNotThrow(() -> {
            var start = System.currentTimeMillis();

            HttpResponse<String> response = client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();

            var end = System.currentTimeMillis() - start;

            assertEquals(200, response.statusCode());
            assertTrue(String.valueOf(body).equals(response.body()));

            System.out.println("request body: " + body + " | response body: " + response.body() + " Request time " + end + "ms");

            CompletableFuture.runAsync(() -> {
                maxRequestTime.accumulateAndGet((int) end, Math::max);
                minRequestTime.accumulateAndGet((int) end, Math::min);
                totalRequestTime.addAndGet((int) end);
            }, Executors.newVirtualThreadPerTaskExecutor());
        });
    }
}