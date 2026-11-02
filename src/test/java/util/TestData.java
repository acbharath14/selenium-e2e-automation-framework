package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads JSON test data from {@code src/test/resources}.
 * Keeps credentials and product expectations out of the test code.
 */
public final class TestData {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestData() {
    }

    private static JsonNode load(String fileName) {
        try (InputStream in = TestData.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new IllegalStateException("Test data file not found: " + fileName);
            }
            return MAPPER.readTree(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read test data: " + fileName, e);
        }
    }

    /** Returns {username, password} for a key in users.json (e.g. "standard"). */
    public static String[] user(String key) {
        JsonNode node = load("users.json").get(key);
        return new String[]{node.get("username").asText(), node.get("password").asText()};
    }

    /** Returns the products array from products.json. */
    public static JsonNode products() {
        return load("products.json").get("products");
    }

    /** Returns the checkout expectations object from products.json. */
    public static JsonNode checkout() {
        return load("products.json").get("checkout");
    }
}
