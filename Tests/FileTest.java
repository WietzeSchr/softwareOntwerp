import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileTest {
    @Test
    void testConstructor() {
        // Tests voor constructor
        File f1 = new File("test1.txt");
        assertEquals(f1.getPath(), "test1.txt");
    }

    @Test
    void testSaveLoadBuffer() throws IOException {
        File f1 = new File("test1.txt");
        f1.save("\r\n", new String[] {"hallo", "iedereen", "", "!"});
        String[] content = f1.load("\r\n");
        assertArrayEquals(content, new String[] {"hallo", "iedereen", "", "!"});
        f1.save("\n", new String[] {"test12", "", "test123"});
        content = f1.load("\n");
        assertArrayEquals(content, new String[] {"test12", "", "test123"});
    }
}