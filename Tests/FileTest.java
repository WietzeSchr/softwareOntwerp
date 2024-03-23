import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileTest {
    @Test
    void fileTest() throws IOException {
        // Tests voor constructor
        File f1 = new File("test1.txt");
        assertEquals(f1.getPath(), "test1.txt");
        // Tests voor safeBuffer
        f1.save("\n", new String[] {"test12", "", "test123"});
        String[] content = f1.load("\n");
        assertEquals(content.length, 3);
        assertEquals(content[0], "test12");
        assertEquals(content[1], "");
        assertEquals(content[2], "test123");
    }
}