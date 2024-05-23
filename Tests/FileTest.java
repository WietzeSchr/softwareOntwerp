import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileTest {
    @Test
    void testConstructor() {
        File f1 = new File("test1.txt");
        assertEquals(f1.getPathString(), "test1.txt");
    }

    @Test
    void testSaveLoadBuffer() throws IOException {
        File f1 = new File("test1.txt");
        f1.save("\n", new String[] {"hallo", String.valueOf((char) 14)}, null);
        assertThrows(RuntimeException.class, () -> f1.load("\n"));
        f1.save("\r\n", new String[] {"hallo", "iedereen", "", "!"}, null);
        String[] content = f1.load("\r\n");
        assertArrayEquals(content, new String[] {"hallo", "iedereen", "", "!"});
        f1.save("\n", new String[] {"test12", "", "test123"}, null);
        content = f1.load("\n");
        assertArrayEquals(content, new String[] {"test12", "", "test123"});

    }

    @Test
    void testNormalizePath() {
        File f1 = new File("../testTxt/test1.txt");
    }
}