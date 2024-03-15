import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileTest {
    @Test
    void fileTest() throws IOException {
        // Tests voor constructor
        FileBuffer fb1 = new FileBuffer(new String[] {"testtest", "1", "", "2"});
        File f1 = new File("test1.txt", fb1);
        assertEquals(f1.getPath(), "test1.txt");
        assertEquals(fb1, f1.getBuffer());
        // Tests voor setters
        FileBuffer fb2 = new FileBuffer(new String[] {"test12", "", "test123"});
        f1.setBuffer(fb2);
        assertEquals(fb2, f1.getBuffer());
        // Tests voor safeBuffer
        f1.saveBuffer("\n");
        FileBuffer fb3 = new FileBuffer("test1.txt", "\n");
        assertEquals(fb3.getContent().length, 3);
        assertEquals(fb3.getContent()[0], "test12");
        assertEquals(fb3.getContent()[1], "");
        assertEquals(fb3.getContent()[2], "test123");
    }
}