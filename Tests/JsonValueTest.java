import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class JsonValueTest {

    @Test
    void testConstructor() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        JsonValue jsonvalue = new JsonValue("testJson", "testBar", new Point(5,5), json);
        assertEquals(jsonvalue.getPathString(), "testJson");
        assertEquals(jsonvalue.getValue(), "testBar");
        assertEquals(jsonvalue.getLocation(), new Point(5,5));
        assertEquals(jsonvalue.getParent(), json);
    }

    @Test
    void testLoad() {
        JsonValue jsonvalue = new JsonValue("testJson", "testBar\ntest  \n!", new Point(5,5));
        assertArrayEquals(jsonvalue.load("\n"), new String[] {"testBar", "test  ", "!"});
    }

    @Test
    void testSave() {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        JsonValue jsonvalue = new JsonValue("testJson", "testBar\ntest  \n!", new Point(5,5), json);
        jsonvalue.save("\r\n", new String[] {"dit", "is", "een", "test"}, new Buffer.Edit[] {});
        assertEquals(jsonvalue.getValue(), "dit\r\nis\r\neen\r\ntest");
        assertArrayEquals(jsonvalue.load("\r\n"), new String[] {"dit", "is", "een", "test"});
    }

    @Test
    void testOpen() throws FileNotFoundException {
        JsonValue jsonvalue = new JsonValue("testJson", "testBar\ntest  \n!", new Point(5,5));
        JsonBuffer buffer = new JsonBuffer(jsonvalue, "\n");
        buffer.setContent(new String[] {"test"});
        FileBufferView view = (FileBufferView) jsonvalue.open(null, null, "\n");
        assertArrayEquals(view.getContent(), new String[] {"testBar", "test  ","!"});
        FileBufferView view2 = (FileBufferView) jsonvalue.open(null, buffer, "\n");
        assertArrayEquals(view2.getContent(), new String[] {"test"});
    }
}