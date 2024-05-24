import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemLeafTest {

    @Test
    void testConstructor() {
        JsonValue jsonvalue = new JsonValue("testJson", "testBar", new Point(5,5));
        assertEquals(jsonvalue.getPathString(), "testJson");
    }

    @Test
    void testGetRoot() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        JsonValue jsonvalue = new JsonValue("testJson", "testBar", new Point(5,5), json);
        json.addToEntries(jsonvalue);
        assertEquals(jsonvalue.getRoot(), json);
    }
}
