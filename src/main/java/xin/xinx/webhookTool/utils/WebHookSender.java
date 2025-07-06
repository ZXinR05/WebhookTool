package xin.xinx.webhookTool.utils;

import com.google.gson.JsonObject;
import xin.xinx.webhookTool.WebhookTool;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WebHookSender {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final List<String> urls = WebhookTool.getInstance().urls;
    private static final Map<String, Boolean> webhookMap = WebhookTool.getInstance().webhookMap;
    private static final String webhookToken = WebhookTool.getInstance().webhookToken;
    private static void send(String url, JsonObject jsonObject) {
        boolean isActive = webhookMap.getOrDefault(jsonObject.get("event").getAsString(), false);
        if (!isActive) {
            WebhookTool.getInstance().debug(jsonObject.get("event").getAsString() + "is inactive");
            return;
        }
        if (webhookToken == null) {
            WebhookTool.getInstance().logger.warning("webhook_token is empty!");
            return;
        }

        String payload = jsonObject.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("X-Webhook-Token", webhookToken)
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        responseFuture.thenAccept(response -> {
            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                WebhookTool.getInstance().debug("Status Code: " + statusCode);
            } else {
                WebhookTool.getInstance().logger.warning("Request failed. Status Code: " + statusCode);
            }
        }).exceptionally(e -> {
            WebhookTool.getInstance().logger.warning("An error occurred: " + e.getMessage());
            return null;
        });
    }
    public static void sendAll(JsonObject jsonObject) {
        for (String url : urls) {
            send(url, jsonObject);
        }
    }
}
