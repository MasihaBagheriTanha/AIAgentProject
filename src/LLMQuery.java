import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OpenAiClient extends AbstractLLMClient {

    private static OpenAiClient instance;

    private OpenAiClient() {
        super("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",
                "https://api.openai.com/v1/chat/completions"
        );
    }

    public static OpenAiClient getInstance() {
        if (instance == null) {
            instance = new OpenAiClient();
        }
        return instance;
    }

    @Override
    public void generateCypherQuery(String userInput) {
        try {
            String prompt = String.format("USER_REQUEST='%s'", userInput);
            String jsonPayload = getJsonPayload(prompt);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(APIURL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + APIKEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode jsonNode = this.parseJson(response.body());
            JsonNode choicesNode = jsonNode.get("choices").get(0).get("message");
            super.query = choicesNode.get("content").asText().trim().replace("`", "");

        } catch (Exception e) {
            e.printStackTrace();
            super.query = "Error generating query";
        }
    }

    public void sendLogToOpenAI() {
        try {
            String logContent = readLogFile("transactions.log");

            if (logContent.isEmpty()) {
                System.out.println("Log file is empty. No data sent to OpenAI.");
                return;
            }

            String jsonPayload = "{\n" +
                    "  \"model\": \"gpt-4\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"You are a log analyzer. Analyze the transactions log and provide insights and suggest purchases for tomorrow (how many for each products).\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + escapeJson(logContent) + "\"}\n" +
                    "  ],\n" +
                    "  \"max_tokens\": 500\n" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(APIURL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + APIKEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Log sent to OpenAI. Response: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending log file to OpenAI.");
        }
    }
    public void sendLogToOpenAIBetterPrompt() {
        try {
            String logContent = readLogFile("transactions.log");

            if (logContent.isEmpty()) {
                System.out.println("Log file is empty. No data sent to OpenAI.");
                return;
            }

            String jsonPayload = "{\n" +
                    "  \"model\": \"gpt-4\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"You are an advanced financial analyst and sales strategist. Analyze the transaction log, predict tomorrow’s prices based on historical trends (assigning higher weights to recent data), and use calculus to determine the optimal way to recharge stockpiles. Your optimization should maximize profit while considering the available budget and existing stock levels.\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + escapeJson(logContent) + "\"}\n" +
                    "  ],\n" +
                    "  \"max_tokens\": 700\n" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(APIURL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + APIKEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Log sent to OpenAI. Response: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending log file to OpenAI.");
        }
    }


    private String readLogFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("Error reading log file: " + e.getMessage());
            return "";
        }
    }

    private String getJsonPayload(String prompt) {
        return "{\n" +
                "  \"model\": \"gpt-4\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"You are a Cypher query generator. Only provide Cypher queries in a single line, without any line breaks or extra formatting.\"},\n" +
                "    {\"role\": \"user\", \"content\": \"" + prompt + "\"}\n" +
                "  ],\n" +
                "  \"max_tokens\": 200\n" +
                "}";
    }
}
