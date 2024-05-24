import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonObjectTest {

    @Test
    void testConstructor() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        assertEquals(json.getBuffer(), bufferContainingJson);
        json.setBuffer(null);
        assertNull(json.getBuffer());
    }

    @Test
    void testAddParent() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        json.addParent(json);
        assertEquals(json.getParent(), json);
        assertEquals(json.getEntries()[0], json);
    }

    @Test
    void testToEntries() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        JsonValue newJsonValue = new JsonValue("test", "testValue", new Point(1,1));
        json.addToEntries(newJsonValue);
        assertEquals(json.getEntries()[0], newJsonValue);
    }

    @Test
    void testGenerateJson() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        assertArrayEquals(json.generateJson(), new String[] {"{", "  \"foo\": \"bar\"", "}"});
    }

    @Test
    void testSaveToBuffer() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        JsonValue fooValue = (JsonValue) json.getEntries()[0];
        fooValue.setValue("testbar");
        json.saveToBuffer(new Buffer.Edit[] {});
    }
}