import org.junit.jupiter.api.*;
import java.io.IOException;

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
    void testSaveToBuffer() throws IOException {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        JsonValue fooValue = (JsonValue) json.getEntries()[0];
        JsonBuffer buffer = new JsonBuffer(fooValue, "\n");
        buffer.addNewChar('t', new Point(1,1), new Point(1,2));
        Buffer.Edit firstEdit = buffer.getLastEdit();
        buffer.addNewChar('e', new Point(1,2), new Point(1,3));
        Buffer.Edit secondEdit = buffer.getLastEdit();
        json.saveToBuffer(new Buffer.Edit[] {firstEdit, secondEdit});
        buffer.saveBuffer("\n");
        assertEquals(fooValue.getValue(), "tebar");
        assertArrayEquals(bufferContainingJson.getContent(), new String[] {"{", "  \"foo\": \"tebar\"", "}"});
        bufferContainingJson.undo();
        assertArrayEquals(bufferContainingJson.getContent(), new String[] {"{", "  \"foo\": \"bar\"", "}"});
        json.close();
        bufferContainingJson.addNewChar('t', new Point(2,11), new Point(2,12));
        assertArrayEquals(bufferContainingJson.getContent(), new String[] {"{", "  \"foo\": \"tbar\"", "}"});
        Buffer.Edit thirdEdit = bufferContainingJson.getLastEdit();
        thirdEdit.mapToStringLocation("\n");
        bufferContainingJson.setContent(new String[]{"{\n  \"foo\": \"tbar\"\n}"});
        bufferContainingJson.undo();
    }
}