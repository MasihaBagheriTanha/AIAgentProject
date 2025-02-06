import java.net.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractLLMClient {

    protected final ObjectMapper objectMapper;
    protected String query = "";
    protected String APIKEY = "";
    protected String APIURL = "";
    protected HttpURLConnection connection;
    protected URL url;

    public AbstractLLMClient(String APIKEY, String APIURL) {
        this.objectMapper = new ObjectMapper();
        this.APIKEY = APIKEY;
        this.APIURL = APIURL;
    }

    public abstract void generateCypherQuery(String userInput);

    public JsonNode parseJson(String jsonString) throws Exception {
        return objectMapper.readTree(jsonString);
    }

    public String escapeJson(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public String getQuery() {
        return query;
    }

    private String extractValue(JsonNode jsonNode, String fieldName) {
        if (jsonNode.has(fieldName)) {
            return jsonNode.get(fieldName).asText().trim().replace("`", "");
        }
        return null;
    }
}